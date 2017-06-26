package com.cbaek.pingpongapp;


import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {PingPongAppModule.class})
public interface PingPongAppComponent {

    void inject(PingPongService pingPongService);

}
