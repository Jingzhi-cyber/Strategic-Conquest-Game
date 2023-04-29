package edu.duke.ece651.team6.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Binary;

import java.io.*;

public class MongoHero {

    MongoClient mongoClient;
    MongoDatabase database;
    MongoCollection<Document> collection;

    private final boolean enable = false;

    private MongoHero() {
        if (!enable) {
            return;
        }
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("risc-db");
        collection = database.getCollection("objects");
    }

    private static final class InstanceHolder {
        static final MongoHero instance = new MongoHero();
    }

    public static MongoHero getInstance() {
        return InstanceHolder.instance;
    }

    public void storeObject(Object object, String key) {
        if (!enable) {
            return;
        }
        byte[] serializedObject;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(object);
            serializedObject = bos.toByteArray();
            Document document = new Document(key, new Binary(serializedObject));
            Document updateDocument = new Document("$set", document);
            // Atomically update the document with the same key, regardless of its existing value
            Document filter = new Document(key, new Document("$exists", true));
            UpdateOptions options = new UpdateOptions().upsert(true);
            collection.updateOne(filter, updateDocument, options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object getObject(String key) {
        if (!enable) {
            return null;
        }
        Bson filter = Filters.exists(key);
        //Document filter = new Document(key, new Document("$exists", true));
        Document document = collection.find(filter).first();
        //Document document = collection.find().first();
        byte[] serializedObject;
        try {
            serializedObject = document.get(key, Binary.class).getData();
        } catch (NullPointerException e) {
            return null;
        }
        try (ByteArrayInputStream bis = new ByteArrayInputStream(serializedObject);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
