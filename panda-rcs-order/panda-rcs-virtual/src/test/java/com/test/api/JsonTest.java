package com.test.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.panda.sport.rcs.virtual.third.client.JSON;
import com.panda.sport.rcs.virtual.third.client.model.CalculationContext;

public class JsonTest {
	
	public static void main(String[] args) throws Exception {
		FileInputStream input = new FileInputStream(new File("C:\\Users\\Administrator\\Desktop\\body.log"));
		int length = input.available();
		byte[] buff = new byte[length];
		input.read(buff, 0, length);
		
		input.close();
		String body = new String(buff,"utf-8");
		System.out.println(body);
		
		JSON json = new JSON();
		Type localVarReturnType = new TypeToken<CalculationContext>(){}.getType();
		
		JsonReader jsonReader = new JsonReader(new StringReader(body));
		jsonReader.setLenient(true);
		CalculationContext context = json.getGson().fromJson(jsonReader, localVarReturnType);
		
//		CalculationContext context = json.deserialize(body, localVarReturnType);
		System.out.println(context);
		
	}

}
