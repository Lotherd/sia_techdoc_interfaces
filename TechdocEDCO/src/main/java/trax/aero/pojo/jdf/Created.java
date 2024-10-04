package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Created {

	@XmlAttribute(name = "ID")
	private String _ID;
	
	@XmlAttribute(name = "AgentName")
	private String _AgentName;
	
	@XmlAttribute(name = "AgentVersion")
	private String _AgentVersion;

	@XmlAttribute(name = "TimeStamp")
	private String _TimeStamp;


	 // Getter Methods 

	 public String get_ID() {
	  return _ID;
	 }

	 public String get_AgentName() {
	  return _AgentName;
	 }

	 public String get_AgentVersion() {
	  return _AgentVersion;
	 }

	 public String get_TimeStamp() {
	  return _TimeStamp;
	 }

	 // Setter Methods 

	 public void set_ID(String _ID) {
	  this._ID = _ID;
	 }

	 public void set_AgentName(String _AgentName) {
	  this._AgentName = _AgentName;
	 }

	 public void set_AgentVersion(String _AgentVersion) {
	  this._AgentVersion = _AgentVersion;
	 }

	 public void set_TimeStamp(String _TimeStamp) {
	  this._TimeStamp = _TimeStamp;
	 }
}
