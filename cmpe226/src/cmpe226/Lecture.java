package cmpe226;


public class Lecture 
{
	int id;
	int partof;
	String topic;
	String filename;
	String type;
	String lectureType;
	String topicDesc;
	
	Lecture(int id, String topic, String filename)
	{
		this.id = id;
		this.topic = topic;
		this.filename = filename;
	}
}
