package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.passiveObjects.ResourcesManager;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;


import java.io.FileReader;
import java.util.concurrent.CountDownLatch;


/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */


public class Main {

	public static void main(String[] args)  {

		// read from Json to a Helper Class
		BagOfPrimitives bagOfPrimitives = new BagOfPrimitives();
		Gson gson = new Gson();
		try {
			bagOfPrimitives = gson.fromJson(new FileReader(args[0]), BagOfPrimitives.class);

		} catch (FileNotFoundException e) {
			System.out.println("deserialize: file path illegal, file not found");
		}

		MessageBusImpl.getInstance(bagOfPrimitives.getEwoks());

		HanSoloMicroservice han = new HanSoloMicroservice();
		C3POMicroservice c3po = new C3POMicroservice();
		R2D2Microservice r2d2 = new R2D2Microservice(bagOfPrimitives.getR2D2());
		LandoMicroservice lando = new LandoMicroservice(bagOfPrimitives.getLando());
		LeiaMicroservice leia = new LeiaMicroservice(bagOfPrimitives.getAttacks());

		Thread tHan = new Thread(han, "han");
		Thread tC3po = new Thread(c3po, "c3po");
		Thread tR2d2 = new Thread(r2d2, "r2d2");
		Thread tLando = new Thread(lando, "lando");
		Thread tLeia = new Thread(leia, "leia");
		tHan.start();
		tC3po.start();
		tR2d2.start();
		tLando.start();

		// leia waits for all threads to be initialized
		try{
			ResourcesManager.latchLeiaInit.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		tLeia.start();

		// wait for all threads to finish
		try{
			ResourcesManager.latchJsonOutput.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// serialize to Json
		Gson gsonOutput = new GsonBuilder().setPrettyPrinting().create();
		try {
			FileWriter writer = new FileWriter(args[1]);
			gsonOutput.toJson(Diary.getInstance(),writer);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.out.println("serialize: failed to serialize json");
		}
	}

}
