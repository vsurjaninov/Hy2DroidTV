# Hy2DroidTV

## Introduction

A self-use Android Hysteria 2 client, fork of [Android-Hysteria-2-client](https://github.com/lry127/Android-Hysteria-2-client), built based on Hysteria 2.5.1 version, optimized for using on Android TV.

## Supported Features

- Very basic Hysteria 2 client functionality, supports exposing SOCKS5 and HTTP ports, supports global proxy
- Custom SNI

## Usage Instructions

- **Server address**: Server domain name or IP
- **Auth**: Password
- **SNI**: The SNI part for TLS. Can be left blank; defaults to extracted from the server address. Can also be different from the server address, in which case the server address is only used as the packet destination, while the actual TLS handshake SNI is specified by this field

## About

- Thanks to [hysteria](https://github.com/apernet/hysteria) for providing the protocol and underlying implementation. [LICENSE](https://github.com/apernet/hysteria/blob/master/LICENSE.md)
- Thanks to [eycorsican/go-tun2socks](https://github.com/eycorsican/go-tun2socks) for providing the proxy implementation. [LICENSE](https://github.com/eycorsican/go-tun2socks/blob/master/LICENSE)
- Thanks to [lry127/Android-Hysteria-2-client](https://github.com/lry127/Android-Hysteria-2-client) for providing the UI implementation. [LICENSE](https://github.com/lry127/Android-Hysteria-2-client/blob/main/LICENSE)

This application is for learning and communication purposes only. Please comply with local laws and regulations when using it.
