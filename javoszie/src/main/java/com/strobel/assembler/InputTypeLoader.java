package com.strobel.assembler;

import com.strobel.assembler.ir.ConstantPool;
import com.strobel.assembler.ir.ConstantPool.TypeInfoEntry;
import com.strobel.assembler.metadata.Buffer;
import com.strobel.assembler.metadata.ClasspathTypeLoader;
import com.strobel.assembler.metadata.ITypeLoader;
import com.strobel.core.StringComparison;
import com.strobel.core.StringUtilities;
import com.strobel.core.VerifyArgument;
import com.strobel.io.PathHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputTypeLoader implements ITypeLoader {
	private static final Logger LOG = Logger.getLogger(InputTypeLoader.class.getSimpleName());
	private final ITypeLoader _defaultTypeLoader;
	private final Map<String, LinkedHashSet<File>> _packageLocations;
	private final Map<String, File> _knownFiles;

	public InputTypeLoader() {
		this(new ClasspathTypeLoader());
	}

	public InputTypeLoader(ITypeLoader defaultTypeLoader) {
		this._defaultTypeLoader = ((ITypeLoader) VerifyArgument.notNull(defaultTypeLoader, "defaultTypeLoader"));
		this._packageLocations = new LinkedHashMap();
		this._knownFiles = new LinkedHashMap();
	}

	public boolean tryLoadType(String typeNameOrPath, Buffer buffer) {
		VerifyArgument.notNull(typeNameOrPath, "typeNameOrPath");
		VerifyArgument.notNull(buffer, "buffer");

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Attempting to load type: " + typeNameOrPath + "...");
		}

		boolean hasExtension = StringUtilities.endsWithIgnoreCase(typeNameOrPath, ".class");

		if ((hasExtension) && (tryLoadFile(null, typeNameOrPath, buffer, true))) {
			return true;
		}

		if (PathHelper.isPathRooted(typeNameOrPath)) {
			if (LOG.isLoggable(Level.FINER)) {
				LOG.finer("Failed to load type: " + typeNameOrPath + ".");
			}
			return false;
		}

		String internalName = hasExtension ? typeNameOrPath.substring(0, typeNameOrPath.length() - 6)
				: typeNameOrPath.replace('.', '/');

		if (tryLoadTypeFromName(internalName, buffer)) {
			return true;
		}

		if (hasExtension) {
			if (LOG.isLoggable(Level.FINER)) {
				LOG.finer("Failed to load type: " + typeNameOrPath + ".");
			}
			return false;
		}

		for (int lastDelimiter = internalName.lastIndexOf('/'); lastDelimiter != -1; lastDelimiter = internalName
				.lastIndexOf('/')) {
			internalName = internalName.substring(0, lastDelimiter) + "$" + internalName.substring(lastDelimiter + 1);

			if (tryLoadTypeFromName(internalName, buffer)) {
				return true;
			}
		}

		if (LOG.isLoggable(Level.FINER)) {
			LOG.finer("Failed to load type: " + typeNameOrPath + ".");
		}

		return false;
	}

	private boolean tryLoadTypeFromName(String internalName, Buffer buffer) {
		if (tryLoadFromKnownLocation(internalName, buffer)) {
			return true;
		}

		if (this._defaultTypeLoader.tryLoadType(internalName, buffer)) {
			return true;
		}

		String filePath = internalName.replace('/', File.separatorChar) + ".class";

		if (tryLoadFile(internalName, filePath, buffer, false)) {
			return true;
		}

		int lastSeparatorIndex = filePath.lastIndexOf(File.separatorChar);

		return (lastSeparatorIndex >= 0)
				&& (tryLoadFile(internalName, filePath.substring(lastSeparatorIndex + 1), buffer, true));
	}

	private boolean tryLoadFromKnownLocation(String internalName, Buffer buffer) {
		File knownFile = (File) this._knownFiles.get(internalName);

		if ((knownFile != null) && (tryLoadFile(knownFile, buffer))) {
			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine("Type loaded from " + knownFile.getAbsolutePath() + ".");
			}
			return true;
		}

		int packageEnd = internalName.lastIndexOf('/');

		String tail;
		String head;
		if ((packageEnd < 0) || (packageEnd >= internalName.length())) {
			head = "";
			tail = internalName;
		} else {
			head = internalName.substring(0, packageEnd);
			tail = internalName.substring(packageEnd + 1);
		}
		for (;;) {
			LinkedHashSet<File> directories = (LinkedHashSet) this._packageLocations.get(head);

			if (directories != null) {
				for (File directory : directories) {
					if (tryLoadFile(internalName, new File(directory, tail + ".class").getAbsolutePath(), buffer,
							true)) {
						return true;
					}
				}
			}

			int split = head.lastIndexOf('/');

			if (split <= 0) {
				break;
			}

			tail = head.substring(split + 1) + '/' + tail;
			head = head.substring(0, split);
		}

		return false;
	}

	private boolean tryLoadFile(File file, Buffer buffer) {
		if (LOG.isLoggable(Level.FINER)) {
			LOG.finer("Probing for file: " + file.getAbsolutePath() + "...");
		}

		if ((!file.exists()) || (file.isDirectory())) {
			return false;
		}
		try {
			FileInputStream in = new FileInputStream(file);
			Throwable localThrowable2 = null;
			try {
				int remainingBytes = in.available();

				buffer.position(0);
				buffer.reset(remainingBytes);
				int bytesRead;
				while (remainingBytes > 0) {
					bytesRead = in.read(buffer.array(), buffer.position(), remainingBytes);

					if (bytesRead < 0) {
						break;
					}

					remainingBytes -= bytesRead;
					buffer.advance(bytesRead);
				}

				buffer.position(0);
				return true;
			} catch (Throwable localThrowable1) {
				localThrowable2 = localThrowable1;
				throw localThrowable1;

			} finally {

				if (in != null)
					if (localThrowable2 != null)
						try {
							in.close();
						} catch (Throwable x2) {
							localThrowable2.addSuppressed(x2);
						}
					else
						in.close();
			}
			
		} catch (IOException e) {
		}
		
		return false;
		
	}

	private boolean tryLoadFile(String internalName, String typeNameOrPath, Buffer buffer, boolean trustName) {
		File file = new File(typeNameOrPath);

		if (!tryLoadFile(file, buffer)) {
			return false;
		}

		String actualName = getInternalNameFromClassFile(buffer);

		String name = trustName ? actualName : internalName != null ? internalName : actualName;

		if (name == null) {
			return false;
		}

		boolean nameMatches = StringUtilities.equals(actualName, internalName);
		boolean pathMatchesName = typeNameOrPath.endsWith(name.replace('/', File.separatorChar) + ".class");

		boolean result = (internalName == null) || (pathMatchesName) || (nameMatches);

		if (result) {
			int packageEnd = name.lastIndexOf('/');
			String packageName;
			if ((packageEnd < 0) || (packageEnd >= name.length())) {
				packageName = "";
			} else {
				packageName = name.substring(0, packageEnd);
			}

			registerKnownPath(packageName, file.getParentFile(), pathMatchesName);

			this._knownFiles.put(actualName, file);

			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine("Type loaded from " + file.getAbsolutePath() + ".");
			}
		} else {
			buffer.reset(0);
		}

		return result;
	}

	private void registerKnownPath(String packageName, File directory, boolean recursive) {
		if ((directory == null) || (!directory.exists())) {
			return;
		}

		LinkedHashSet<File> directories = (LinkedHashSet) this._packageLocations.get(packageName);

		if (directories == null) {
			this._packageLocations.put(packageName, directories = new LinkedHashSet());
		}

		if ((!directories.add(directory)) || (!recursive)) {
			return;
		}
		try {
			String directoryPath = StringUtilities
					.removeRight(directory.getCanonicalPath(),
							new char[] { PathHelper.DirectorySeparator, PathHelper.AlternateDirectorySeparator })
					.replace('\\', '/');

			String currentPackage = packageName;
			File currentDirectory = new File(directoryPath);

			int delimiterIndex;
			while (((delimiterIndex = currentPackage.lastIndexOf('/')) >= 0) && (currentDirectory.exists())
					&& (delimiterIndex < currentPackage.length() - 1)) {

				String segmentName = currentPackage.substring(delimiterIndex + 1);

				if (!StringUtilities.equals(currentDirectory.getName(), segmentName,
						StringComparison.OrdinalIgnoreCase)) {
					break;
				}

				currentPackage = currentPackage.substring(0, delimiterIndex);
				currentDirectory = currentDirectory.getParentFile();

				directories = (LinkedHashSet) this._packageLocations.get(currentPackage);

				if (directories == null) {
					this._packageLocations.put(currentPackage, directories = new LinkedHashSet());
				}

				if (!directories.add(currentDirectory)) {
					break;
				}
			}
		} catch (IOException ignored) {
		}
	}

	private static String getInternalNameFromClassFile(Buffer b) {
		long magic = b.readInt() & 0xFFFFFFFF;

		if (magic != 3405691582L) {
			return null;
		}

		b.readUnsignedShort();
		b.readUnsignedShort();

		ConstantPool constantPool = ConstantPool.read(b);

		b.readUnsignedShort();

		ConstantPool.TypeInfoEntry thisClass = (ConstantPool.TypeInfoEntry) constantPool
				.getEntry(b.readUnsignedShort());

		b.position(0);

		return thisClass.getName();
	}
}
