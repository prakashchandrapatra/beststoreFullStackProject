package com.example.beststore;

import java.sql.Connection;
import java.sql.DriverManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BeststoreApplication {

	public static void main(String[] args) {
		String host = System.getenv("DB_HOST");
        String port = System.getenv("DB_PORT");
        String database = System.getenv("DB_NAME");
        String username = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        System.out.println("========== DATABASE TEST ==========");
        System.out.println("HOST = [" + host + "]");
        System.out.println("PORT = [" + port + "]");
        System.out.println("DATABASE = [" + database + "]");
        System.out.println("USER = [" + username + "]");

        try {

            System.out.println("DNS = " +
                java.net.InetAddress.getByName(host));

            String url =
                "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useSSL=true&serverTimezone=UTC";

            System.out.println("JDBC URL = " + url);

            Connection connection =
                DriverManager.getConnection(url, username, password);

            System.out.println("==================================");
            System.out.println("MYSQL CONNECTION SUCCESSFUL!");
            System.out.println("==================================");

            connection.close();

        } catch (Exception e) {

            System.out.println("==================================");
            System.out.println("MYSQL CONNECTION FAILED!");
            System.out.println("==================================");

            e.printStackTrace();
        }

		
		SpringApplication.run(BeststoreApplication.class, args);
	}

}
