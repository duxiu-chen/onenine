package me.mucool.testapplication.network.factory;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import me.mucool.testapplication.network.exception.CustomException;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import static okhttp3.internal.Util.UTF_8;


public class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            String response = value.string();
            JsonReader jsonReader = null;
            MediaType mediaType = value.contentType();
            Charset charset = mediaType != null ? mediaType.charset(UTF_8) : UTF_8;
            InputStream inputStream = new ByteArrayInputStream(response.getBytes());
            jsonReader = gson.newJsonReader(new InputStreamReader(inputStream, charset));
            System.out.println(response);
            return adapter.read(jsonReader);
        }catch (Exception e){
            e.printStackTrace();
            throw new CustomException(e.getMessage());
        } finally {
            value.close();
        }

    }

}