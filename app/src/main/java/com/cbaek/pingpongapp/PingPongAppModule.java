package com.cbaek.pingpongapp;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PingPongAppModule {

    @Provides
    @Singleton
    public PingPongClient providePingPongClient() {
        return new PingPongClient();
    }

}
