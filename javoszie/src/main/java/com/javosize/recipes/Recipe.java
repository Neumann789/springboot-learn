 package com.javosize.recipes;
 
 import com.javosize.log.Log;
 import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
 import java.io.IOException;
 import java.io.Serializable;
 import java.io.StringReader;
 import java.io.StringWriter;
 import java.io.Writer;
 import java.util.ArrayList;
 import java.util.List;
 import javax.xml.bind.JAXBContext;
 import javax.xml.bind.Marshaller;
 import javax.xml.bind.Unmarshaller;
 import javax.xml.bind.annotation.XmlElement;
 import javax.xml.bind.annotation.XmlElementWrapper;
 import javax.xml.bind.annotation.XmlRootElement;
 import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
 import org.json.JSONArray;
 import org.json.JSONObject;
 
 
 
 
 
 
 @XmlRootElement
 public class Recipe
   implements Serializable
 {
   private static final long serialVersionUID = -6486187731207891682L;
   private static Log log = new Log(Recipe.class.getName());
   
   private String name;
   
   private String code;
   
   private String author;
   
   private String description;
   private List<RecipeParam> paramDescriptions;
   private String paramDescription;
   private int numberOfParameters;
   
   public int getNumberOfParameters()
   {
     return this.numberOfParameters;
   }
   
   @XmlElement
   public void setNumberOfParameters(int numberOfParameters) {
     this.numberOfParameters = numberOfParameters;
   }
   
   public String getName() {
     return this.name;
   }
   
   @XmlElement
   public void setName(String name) {
     this.name = name;
   }
   
   public String getCode() {
     return this.code;
   }
   
   @XmlJavaTypeAdapter(XMLAdapterCDATA.class)
   public void setCode(String code) {
     this.code = code;
   }
   
   public String getAuthor() {
     return this.author;
   }
   
   @XmlElement
   public void setAuthor(String author) {
     this.author = author;
   }
   
   public String getDescription() {
     return this.description;
   }
   
   @XmlElement
   public void setDescription(String description) {
     this.description = description;
   }
   
   @Deprecated
   public String getParamDescription() {
     return this.paramDescription;
   }
   
   @Deprecated
   public void setParamDescription(String paramDescription) {
     this.paramDescription = paramDescription;
   }
   
   public List<RecipeParam> getParamDescriptions() {
     return this.paramDescriptions;
   }
   
   @XmlElementWrapper
   @XmlElement(name="param")
   public void setParamDescriptions(List<RecipeParam> paramDescriptions) {
     this.paramDescriptions = paramDescriptions;
   }
   
   public String toJSON() {
     JSONObject json = new JSONObject(this);
     return json.toString(3);
   }
   
   public static Recipe fromJSON(String json) throws IOException {
     Recipe r = null;
     try {
       r = new Recipe();
       JSONObject jsonObject = new JSONObject(json);
       
       if (jsonObject.has("name")) {
         r.setName(jsonObject.getString("name"));
       } else {
         throw new Exception("Missing attribute \"name\" in json.");
       }
       
       if (jsonObject.has("author")) {
         r.setAuthor(jsonObject.getString("author"));
       } else {
         throw new Exception("Missing attribute \"author\" in json.");
       }
       
       if (jsonObject.has("code")) {
         r.setCode(jsonObject.getString("code"));
       } else {
         throw new Exception("Missing attribute \"code\" in json.");
       }
       
       if (jsonObject.has("description")) {
         r.setDescription(jsonObject.getString("description"));
       } else {
         throw new Exception("Missing attribute \"description\" in json.");
       }
       
       if (jsonObject.has("paramDescriptions")) {
         JSONArray paramDescJS = jsonObject.getJSONArray("paramDescriptions");
         ArrayList<RecipeParam> paramDescriptions = new ArrayList();
         for (int i = 0; i < paramDescJS.length(); i++) {
           JSONObject param = paramDescJS.getJSONObject(i);
           paramDescriptions.add(new RecipeParam(param
           
             .getInt("id"), param
             .getString("description")));
         }
         
         r.setParamDescriptions(paramDescriptions);
       }
       else if (jsonObject.has("paramDescription")) {
         String descriptions = jsonObject.getString("paramDescription");
         ArrayList<RecipeParam> paramDescriptions = new ArrayList();
         if ((descriptions != null) && (!descriptions.isEmpty())) {
           String[] params = descriptions.split("\n");
           for (int i = 0; i < params.length; i++) {
             String[] info = params[i].split(":");
             int id = Integer.valueOf(info[0].replace("{", "").replace("}", "").trim()).intValue();
             String desc = info[1].trim();
             paramDescriptions.add(new RecipeParam(id, desc));
           }
         }
         r.setParamDescriptions(paramDescriptions);
       } else {
         throw new Exception("Missing attribute \"paramDescriptions\" in json.");
       }
       
       if (jsonObject.has("numberOfParameters")) {
         r.setNumberOfParameters(jsonObject.getInt("numberOfParameters"));
         
         if (r.getNumberOfParameters() != r.getParamDescriptions().size()) {
           throw new Exception("The number of paramaters descriptions doesn't match the number of parameters defined.");
         }
       } else {
         throw new Exception("Missing attribute \"numberOfParameters\" in json.");
       }
     }
     catch (Throwable th) {
       log.trace("Exception parsing JSon: " + th, th);
       throw new IOException("Error parsing JSon: " + th);
     }
     return r;
   }
   
   public String toXML() {
     StringWriter writer = null;
     try {
       JAXBContext jaxbContext = JAXBContext.newInstance(new Class[] { Recipe.class });
       Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
       jaxbMarshaller.setProperty("jaxb.formatted.output", Boolean.valueOf(true));
       jaxbMarshaller.setProperty(CharacterEscapeHandler.class.getName(), new CharacterEscapeHandler()
       {
         public void escape(char[] ac, int i, int j, boolean flag, Writer writer)
           throws IOException
         {
           writer.write(ac, i, j);
         }
       });
       writer = new StringWriter();
       jaxbMarshaller.marshal(this, writer);
       return writer.toString();
     } catch (Throwable th) {
       log.error("Unable to export recipe " + this.name + " to XML: " + th, th);
       return "Unable to export recipe " + this.name + " to XML: " + th + "\n";
     } finally {
       if (writer != null) {
         try { writer.close();
         } catch (Throwable localThrowable3) {}
       }
     }
   }
   
   public static Recipe fromXML(String xml) throws IOException {
     Recipe recipe = null;
     StringReader reader = null;
     try {
       JAXBContext jaxbContext = JAXBContext.newInstance(new Class[] { Recipe.class });
       
       Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
       reader = new StringReader(xml);
       recipe = (Recipe)jaxbUnmarshaller.unmarshal(reader);
     } catch (Throwable th) {
       log.trace("Unable to import recipe from XML: " + th, th);
       throw new IOException("Error parsing xml: " + th);
     } finally {
       if (reader != null) { reader.close();
       }
     }
     return recipe;
   }
 }


