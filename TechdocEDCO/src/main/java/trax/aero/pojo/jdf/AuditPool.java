package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class AuditPool {
	
 	@XmlElement(name = "Created")
	Created CreatedObject;


	 // Getter Methods 

	 public Created getCreated() {
	  return CreatedObject;
	 }

	 // Setter Methods 

	 public void setCreated(Created CreatedObject) {
	  this.CreatedObject = CreatedObject;
	 }
}
