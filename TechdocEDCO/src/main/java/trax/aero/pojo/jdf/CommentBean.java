package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;





public class CommentBean {
    String name="";
   String Comment="";
  public void setName(String name) { 
		this.name=name;
	} 
    @XmlAttribute(name = "Name")
    public String getName() { 
		return name;
	}
    
    @XmlValue
	public String getComment() {
		return Comment;
	}
	public void setComment(String Comment) {
		this.Comment = Comment;
	} 
 

}