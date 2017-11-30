import org.bson.Document;
import org.w3c.dom.ranges.DocumentRange;

import com.mongodb.*;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoCredential;
import com.mongodb.MongoClientOptions; 
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;


public class mongoDBConnector {

	private MongoClient mongoClient;

	// TODO Setup Maven to install MongoDB Dependencies.
	// TODO Update Maven MongoDB Dependencies.
	// TODO MongoDB Driver for connecting to MongoDB Sever Instance.

	mongoDBConnector(){
		mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		// Accessing the databases.
		MongoDatabase db = mongoClient.getDatabase("yelp");
		System.out.println("Connected to the database successfully");  	
		//Accessing the collections.
		
		MongoCollection<Document> coll = db.getCollection("business");
		System.out.println(coll.count());
		//coll.find(equals(obj)))
		coll.find(eq("categories","Restaurants"));
		//FindIterable<Document> iterDoc = coll.find();
		//int i=1;
		//Iterator it =  iterDoc.iterator();
		
	}
}
