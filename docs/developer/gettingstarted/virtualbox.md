# Create VirtualBox appliance for Kitodo 4.x

This guide creates a [VirtualBox](https://www.virtualbox.org/) appliance with a development and test instance of Kitodo 4.x.

*Warning: This appliance is intended for development and tests in local networks only. Do not use it in production mode!*

## Download Ubuntu 24.04 LTS ISO file

Download `ubuntu-24.04.x-live-server-amd64.iso` from <https://ubuntu.com/download/server>.

## Create Virtual Machine

* Name: `kitodo 4.1.0`
* Type: `Linux`
* Version: `Ubuntu (64-bit)`
* Memory size: `4096 MB`
* Hard disk: `VDI` / `dynamically allocated` / `20 GB`

## Virtual Machine settings

* General/Advanced/Shared clipboard: `Bidirectional`
* System/Processor/Processor(s): `2`
* Display/Screen/Video Memory: `128 MB`
* Network/Adapter 1/Advanced/Port Forwarding/+
  * Host Port: `8080`
  * Guest Port: `8080`

## Start Virtual Machine

Select the downloaded ISO file and install Ubuntu 24.04:

* Language: `English`
* Hostname: `kitodo`
* Root/regular user and password: create user `kitodo` (password `kitodo`), enable `Log in automatically`
* Server software: `OpenSSH server`

## Install VirtualBox guest additions (shared clipboard) and reboot

```
sudo apt update && sudo apt install -y virtualbox-guest-utils
sudo reboot
```

## Install Kitodo

Follow the installation instructions in [Build development version](development-version.md).

Make sure to install Java 21 (`openjdk-21-jdk`), Tomcat 10, MySQL 8 and OpenSearch 2.x.

## Create shortcuts

* Website link
```
echo '[Desktop Entry]
Encoding=UTF-8
Name=Kitodo.Production
Type=Link
URL=http://localhost:8080/kitodo/
Icon=text-html' >> ~/Desktop/Kitodo.Production.desktop
```
* Symlinks to folders
```
ln -s /usr/local/kitodo ~/Desktop/kitodo-config
ln -s /var/lib/tomcat10/webapps/ ~/Desktop/kitodo-app
```

## Save password in Firefox

* Log in at <http://localhost:8080/kitodo/pages/login> with user `testAdmin` and password `test`
* Click `save` in the popup dialog to let Firefox save this login

## Export Appliance

VirtualBox Manager / File / Export Appliance

* File: `kitodo-production-4.1.0.ova`
* Product: `Kitodo Production`
* Product-URL: `http://www.kitodo.org`
* Version: `4.1.0`
* Description:
```
This VirtualBox appliance is intended for development and tests in local networks. Do not use it in production mode!

The Kitodo.Production webapp should be available from guest and host system (via NAT Port Forwarding) at:
* http://localhost:8080/kitodo/
* user: testAdmin
* pass: test

The appliance is based on Ubuntu 24.04 LTS, openjdk-21, tomcat10, mysql 8 and OpenSearch 2.x
* system user: kitodo
* system user password: kitodo
* mysql user: kitodo
* mysql user password: kitodo
* mysql root password: kitodo
```
* License: `GPLv3 https://www.gnu.org/licenses/gpl-3.0.en.html`
