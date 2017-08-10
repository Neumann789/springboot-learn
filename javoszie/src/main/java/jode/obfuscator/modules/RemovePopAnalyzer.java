package jode.obfuscator.modules;

import java.util.List;
import java.util.ListIterator;
import jode.AssertError;
import jode.bytecode.BytecodeInfo;
import jode.bytecode.Handler;
import jode.bytecode.Instruction;
import jode.bytecode.Opcodes;
import jode.bytecode.Reference;
import jode.bytecode.TypeSignature;
import jode.obfuscator.CodeTransformer;

public class RemovePopAnalyzer
  implements CodeTransformer, Opcodes
{
  public void transformCode(BytecodeInfo paramBytecodeInfo)
  {
    int[] arrayOfInt = new int[2];
    ListIterator localListIterator = paramBytecodeInfo.getInstructions().listIterator();
    while (localListIterator.hasNext())
    {
      Instruction localInstruction1 = (Instruction)localListIterator.next();
      int i = 0;
      switch (localInstruction1.getOpcode())
      {
      case 0: 
        localListIterator.remove();
        break;
      case 88: 
        i = 1;
      case 87: 
        if (localInstruction1.getPreds() == null)
        {
          Handler[] arrayOfHandler = paramBytecodeInfo.getExceptionHandlers();
          for (int j = 0;; j++)
          {
            if (j >= arrayOfHandler.length) {
              break label138;
            }
            if (arrayOfHandler[j].catcher == localInstruction1) {
              break;
            }
          }
          label138:
          localListIterator.remove();
          Instruction localInstruction2 = (Instruction)localListIterator.previous();
          Instruction localInstruction3 = localInstruction2;
          int k = 0;
          int m;
          for (;;)
          {
            if ((localInstruction3.getSuccs() != null) || (localInstruction3.doesAlwaysJump()))
            {
              localInstruction3 = null;
              break label343;
            }
            localInstruction3.getStackPopPush(arrayOfInt);
            if (k < arrayOfInt[1])
            {
              if (k == 0) {
                break label343;
              }
              m = localInstruction3.getOpcode();
              if ((k <= 3) && (m == 89 + k - 1))
              {
                localListIterator.remove();
                if (i == 0) {
                  break;
                }
                localInstruction1 = new Instruction(87);
                i = 0;
                localInstruction3 = (Instruction)localListIterator.previous();
                continue;
              }
              if ((i != 0) && (k > 1) && (k <= 4) && (m == 92 + k - 2))
              {
                localListIterator.remove();
                break;
              }
              localInstruction3 = null;
              break label343;
            }
            k += arrayOfInt[0] - arrayOfInt[1];
            localInstruction3 = (Instruction)localListIterator.previous();
          }
          label343:
          while (localListIterator.next() != localInstruction2) {}
          if ((i == 0) && (localInstruction2.getOpcode() == 87))
          {
            localListIterator.set(new Instruction(88));
          }
          else
          {
            localListIterator.add(localInstruction1);
            continue;
            m = localInstruction3.getOpcode();
            switch (m)
            {
            case 20: 
            case 22: 
            case 24: 
              if (i == 0) {
                throw new AssertError("pop on long");
              }
              localListIterator.remove();
              break;
            case 18: 
            case 21: 
            case 23: 
            case 25: 
            case 89: 
            case 187: 
              if (i != 0) {
                localListIterator.set(new Instruction(87));
              } else {
                localListIterator.remove();
              }
              break;
            case 46: 
            case 48: 
            case 50: 
            case 51: 
            case 52: 
            case 53: 
            case 96: 
            case 98: 
            case 100: 
            case 102: 
            case 104: 
            case 106: 
            case 108: 
            case 110: 
            case 112: 
            case 114: 
            case 120: 
            case 122: 
            case 124: 
            case 126: 
            case 128: 
            case 130: 
            case 149: 
            case 150: 
              localListIterator.next();
              localListIterator.add(localInstruction1);
              localListIterator.previous();
              localListIterator.previous();
              localListIterator.set(new Instruction(87));
              break;
            case 90: 
              localListIterator.set(new Instruction(95));
              localListIterator.next();
              if (i != 0) {
                localListIterator.add(new Instruction(87));
              }
              break;
            case 92: 
              if (i != 0) {
                localListIterator.remove();
              }
              break;
            case 95: 
              if (i != 0) {
                localListIterator.set(localInstruction1);
              }
              break;
            case 47: 
            case 49: 
            case 117: 
            case 119: 
            case 138: 
            case 143: 
              if (i == 0) {
                throw new AssertError("pop on long");
              }
            case 116: 
            case 118: 
            case 134: 
            case 139: 
            case 145: 
            case 146: 
            case 147: 
            case 188: 
            case 189: 
            case 190: 
            case 193: 
              localListIterator.set(localInstruction1);
              break;
            case 136: 
            case 137: 
            case 142: 
            case 144: 
              if (i != 0)
              {
                localListIterator.next();
                localListIterator.add(new Instruction(87));
                localListIterator.previous();
                localListIterator.previous();
              }
              localListIterator.set(new Instruction(88));
              break;
            case 97: 
            case 99: 
            case 101: 
            case 103: 
            case 105: 
            case 107: 
            case 109: 
            case 111: 
            case 113: 
            case 115: 
            case 127: 
            case 129: 
            case 131: 
              if (i == 0) {
                throw new AssertError("pop on long");
              }
              localListIterator.next();
              localListIterator.add(localInstruction1);
              localListIterator.previous();
              localListIterator.previous();
              localListIterator.set(new Instruction(88));
              break;
            case 121: 
            case 123: 
            case 125: 
              if (i == 0) {
                throw new AssertError("pop on long");
              }
              localListIterator.next();
              localListIterator.add(localInstruction1);
              localListIterator.previous();
              localListIterator.previous();
              localListIterator.set(new Instruction(87));
              break;
            case 133: 
            case 135: 
            case 140: 
            case 141: 
              if (i == 0) {
                throw new AssertError("pop on long");
              }
              localListIterator.set(new Instruction(87));
              break;
            case 148: 
            case 151: 
            case 152: 
              localListIterator.next();
              localListIterator.add(new Instruction(88));
              if (i != 0)
              {
                localListIterator.add(new Instruction(87));
                localListIterator.previous();
              }
              localListIterator.previous();
              localListIterator.previous();
              localListIterator.set(new Instruction(88));
              break;
            case 178: 
            case 180: 
              Reference localReference = localInstruction3.getReference();
              int i1 = TypeSignature.getTypeSize(localReference.getType());
              if ((i1 == 2) && (i == 0)) {
                throw new AssertError("pop on long");
              }
              if (m == 180) {
                i1--;
              }
              switch (i1)
              {
              case 0: 
                localListIterator.set(localInstruction1);
                break;
              case 1: 
                if (i != 0) {
                  localListIterator.set(new Instruction(87));
                }
                break;
              case 2: 
                localListIterator.remove();
              }
              break;
            case 197: 
              int n = localInstruction3.getDimensions();
              n--;
              if (n > 0)
              {
                localListIterator.next();
                while (n-- > 0)
                {
                  localListIterator.add(new Instruction(87));
                  localListIterator.previous();
                }
                localListIterator.previous();
              }
              localListIterator.set(localInstruction1);
              break;
            case 182: 
            case 183: 
            case 184: 
            case 185: 
            case 192: 
              if ((TypeSignature.getReturnSize(localInstruction3.getReference().getType()) == 1) && (i != 0))
              {
                localListIterator.next();
                localListIterator.add(new Instruction(87));
                localListIterator.add(new Instruction(87));
                localListIterator.previous();
              }
              break;
            case 19: 
            case 26: 
            case 27: 
            case 28: 
            case 29: 
            case 30: 
            case 31: 
            case 32: 
            case 33: 
            case 34: 
            case 35: 
            case 36: 
            case 37: 
            case 38: 
            case 39: 
            case 40: 
            case 41: 
            case 42: 
            case 43: 
            case 44: 
            case 45: 
            case 54: 
            case 55: 
            case 56: 
            case 57: 
            case 58: 
            case 59: 
            case 60: 
            case 61: 
            case 62: 
            case 63: 
            case 64: 
            case 65: 
            case 66: 
            case 67: 
            case 68: 
            case 69: 
            case 70: 
            case 71: 
            case 72: 
            case 73: 
            case 74: 
            case 75: 
            case 76: 
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 86: 
            case 87: 
            case 88: 
            case 91: 
            case 93: 
            case 94: 
            case 132: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 166: 
            case 167: 
            case 168: 
            case 169: 
            case 170: 
            case 171: 
            case 172: 
            case 173: 
            case 174: 
            case 175: 
            case 176: 
            case 177: 
            case 179: 
            case 181: 
            case 186: 
            case 191: 
            case 194: 
            case 195: 
            case 196: 
            default: 
              localListIterator.next();
              localListIterator.add(localInstruction1);
            }
          }
        }
        break;
      }
    }
  }
}


