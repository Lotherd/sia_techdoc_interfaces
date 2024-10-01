package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "JDF")
public class JDF {

	 	@XmlElement(name = "Comment")
	    private Comment Comment;
	    
	 	@XmlElement(name = "ResourcePool")
	    private ResourcePool ResourcePool;
	 	
	 	@XmlElement(name = "ResourceLinkPool")
	    private ResourceLinkPool ResourceLinkPool;
	 	
	 	@XmlElement(name = "AuditPool")
	    private AuditPool AuditPool;
	 	
	 	@XmlAttribute(name = "ID")
	    private String ID;
	 	
	 	@XmlAttribute(name = "Type")
	    private String Type;
	 	
	 	@XmlAttribute(name = "Category")
	    private String Category;
	 	
	 	@XmlAttribute(name = "Types")
	    private String Types;
	 	
	 	@XmlAttribute(name = "Activation")
	    private String Activation;
	 	
	 	@XmlAttribute(name = "Status")
	    private String Status;
	 	
	 	@XmlAttribute(name = "Version")
	    private String Version;
	 	
	 	@XmlAttribute(name = "DescriptiveName")
	    private String DescriptiveName;

		public Comment getComment() {
			return Comment;
		}

		public void setComment(Comment comment) {
			Comment = comment;
		}

		public ResourcePool getResourcePool() {
			return ResourcePool;
		}

		public void setResourcePool(ResourcePool resourcePool) {
			ResourcePool = resourcePool;
		}

		public ResourceLinkPool getResourceLinkPool() {
			return ResourceLinkPool;
		}

		public void setResourceLinkPool(ResourceLinkPool resourceLinkPool) {
			ResourceLinkPool = resourceLinkPool;
		}

		public AuditPool getAuditPool() {
			return AuditPool;
		}

		public void setAuditPool(AuditPool auditPool) {
			AuditPool = auditPool;
		}

		public String getID() {
			return ID;
		}

		public void setID(String iD) {
			ID = iD;
		}

		public String getType() {
			return Type;
		}

		public void setType(String type) {
			Type = type;
		}

		public String getCategory() {
			return Category;
		}

		public void setCategory(String category) {
			Category = category;
		}

		public String getTypes() {
			return Types;
		}

		public void setTypes(String types) {
			Types = types;
		}

		public String getActivation() {
			return Activation;
		}

		public void setActivation(String activation) {
			Activation = activation;
		}

		public String getStatus() {
			return Status;
		}

		public void setStatus(String status) {
			Status = status;
		}

		public String getVersion() {
			return Version;
		}

		public void setVersion(String version) {
			Version = version;
		}

		public String getDescriptiveName() {
			return DescriptiveName;
		}

		public void setDescriptiveName(String descriptiveName) {
			DescriptiveName = descriptiveName;
		}
	 	
	 	
}
