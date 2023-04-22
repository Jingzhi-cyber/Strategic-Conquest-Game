package edu.duke.ece651.team6.server.repository;


import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.Query;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

public class Test {

    public static void main(String[] args) {
//        Datastore datastore = Morphia.createDatastore(MongoClients.create(), "class-store-test");
//        datastore.getMapper().mapPackage("edu.duke.ece651.team6.server.repository");
//        datastore.ensureIndexes();
        Car car1 = new Car("1", "toyota", "red", 2010);
        Car car2 = new Car("2", "honda", "blue", 2011);
//        datastore.save(car1);
//        datastore.save(car2);
//        car1.setName("dajiba");
//        datastore.save(car1);
        Map<Car, Map<Car, Integer>> map = new HashMap<>();
        map.put(car1, new HashMap<>());
        map.get(car1).put(car2, 5);
        map.put(car2, new HashMap<>());
        map.get(car2).put(car1, 10);
        TestEntity testEntity = new TestEntity("1", "test", car1, car1, map);
//        datastore.save(testEntity);
//        Query<TestEntity> query = datastore.find(TestEntity.class);
//        TestEntity testEntity1 = query.first();
//        System.out.println(testEntity1.getCar().getName());
//        Car car3 = testEntity1.getCar();
//        Car car4 = testEntity1.getAnotherCar();
//        System.out.println(car3 == car4);
//        map = testEntity1.getMap();
//        System.out.println(map.keySet().contains(car1));
//
        MongoHero mongoHero = MongoHero.getInstance();
        mongoHero.storeObject(testEntity, "testEntity");
        TestEntity testEntity1 = (TestEntity) mongoHero.getObject("testEntity");
        System.out.println(testEntity1.getMap().containsKey(testEntity1.getCar()));
        System.out.println(testEntity1.getName());
        System.out.println(mongoHero.getObject("hello"));
    }
}
