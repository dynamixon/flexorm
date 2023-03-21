package io.github.dynamixon.test

import groovy.transform.builder.Builder

@Builder
class ConnInfo {
    String url
    String username
    String password
}
