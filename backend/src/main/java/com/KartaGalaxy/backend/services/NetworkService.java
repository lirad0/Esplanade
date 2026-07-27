package com.KartaGalaxy.backend.services;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.stereotype.Service;

@Service
public class NetworkService {
    public String getContainerExternalIP() {
        try {
            // Resolve Docker's special DNS record for the host machine
            InetAddress hostAddress = InetAddress.getByName("host.docker.internal");
            String hostLanIp = hostAddress.getHostAddress();
            
            return hostLanIp;
        } catch (UnknownHostException e) {
            return "localhost";
        }
    }
}
