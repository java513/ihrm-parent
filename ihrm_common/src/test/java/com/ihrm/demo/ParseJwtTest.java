package com.ihrm.demo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

/**
 * @author liuhao
 * @Desc //
 * @DATE 2019/12/22 22:58
 */
public class ParseJwtTest {
    public static void main(String[] args) {
        String token="eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMjMiLCJzdWIiOiLojIPotLEiLCJpYXQiOjE1NzcwMj" +
                "Y2NDF9.ZPHNZV1L5ytRVEO-K5JKKksIHFX8znZPq5z3ilvZ7Bc";
        Claims claims = Jwts.parser().setSigningKey("ihrm").parseClaimsJws(token).getBody();
        System.out.println("id:"+claims.getId());
        System.out.println("subject:"+claims.getSubject());
        System.out.println("time"+claims.getIssuedAt());
    }
}
