import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static String KEY_HTTPS = "https://api.nasa.gov/planetary/apod?api_key=vPTX7ClFaiUDfHUrmho4X8RIuY8F019hpZTBMVwS";
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();
        HttpGet request = new HttpGet(KEY_HTTPS);
        CloseableHttpResponse response = httpClient.execute(request);

        ResponseNASA responseNASA = mapper.readValue(
                response.getEntity().getContent(), new TypeReference<>() {
                });

        System.out.println("Ссылка на скачиваемый файл:" + responseNASA.getUrl());//проверим ссылку

        String nameFile = responseNASA.getUrl().split("/")[responseNASA.getUrl().split("/").length - 1];//выдернем название и тип файла
        System.out.println("имя файла: " + nameFile);//проверим

        //закачаем файл
        try (InputStream in = URI.create(responseNASA.getUrl()).toURL().openStream()) {
            Files.copy(in, Paths.get("src/main/resources/" + nameFile));
            System.out.println("Медиа загружены");
        }
    }
}