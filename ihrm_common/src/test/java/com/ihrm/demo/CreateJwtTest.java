package com.ihrm.demo;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

/**
 * @author liuhao
 * @Desc //
 * @DATE 2019/12/22 22:52
 */
public class CreateJwtTest {
    public static void main(String[] args) {
        JwtBuilder jwtBuilder = Jwts.builder().setId("123").setSubject("范贱")
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, "ihrm");
        String token = jwtBuilder.compact();
        System.out.println(token);
    }
}
