package com.bsrvch.arbita.dto.crypto;

import java.util.HashMap;

public class Coin {
    private String name;
    private String description;
    private HashMap<String, Network> networks = new HashMap<>();


    public Coin(String name, String description, HashMap<String, Network> networks) {
        this.name = name;
        this.description = description;
        this.networks = networks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashMap<String, Network> getNetworks() {
        return networks;
    }

    public void setNetworks(HashMap<String, Network> networks) {
        this.networks = networks;
    }
    public void addNetwork(Network network){
        networks.put(network.getName(),network);
    }
}
