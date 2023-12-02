package com.backend.playground.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Keys {
    public ArrayList<Key> keys;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Key {
    public String n;
    public String alg;
    public String kty;
    public String e;
    public String kid;
    public String use;
}