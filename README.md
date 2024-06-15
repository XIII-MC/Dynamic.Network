# This documention is outdated and no longer needed in newer versions.

# Dynamic.Network

Dynamic.Network (DCN) allows you to quickly scan any network and its open ports.

## Usage

DNC was compiled using **Java 8 (JDK 1.8)**, if you experience errors or issues please try this java version before opening an issue.
To tell which IP to scan it uses one of your network cards, if you have for exemple WiFi cards, Virtual Cards or such make sure to disable them before starting the scan to make sure DCN scans the right network.
For now only **IPv4** is supported.

##

- Help command:

```bash
java -jar Dynamic.Network.jar help
```
```
Welcome to Dynamic.Network (DCN) vD1!
arg 0 = help | Show this page
arg 0 = scan=<IP> | Scan a precise IP scope
arg 0-99 = auto=true | Automatically start a port scan whenever an IP is reachable in your network.
arg 0-99 = dport=true | Scan the dynamic ports range (49152-65535)
No args | Basic network scan without port scanning
```

##

- Basic network scan
```bash
java -jar Dynamic.Network.jar
```
```
Host '192.168.1.99' is reachable
Host '192.168.1.1' is reachable
Host '192.168.1.100' is reachable
Host '192.168.1.11' is reachable
Finished network scan in 4988ms!
```

##

- Precise IP scope port scan *(Without dynamic ports)*
```bash
java -jar Dynamic.Network.jar scan=192.168.1.100
```
```
Starting port scan on '192.168.1.99'... (AUTO 49152 THREADS)
          Port '135' is open on '192.168.1.99'
          Port '139' is open on '192.168.1.99'
          Port '445' is open on '192.168.1.99'
          Port '5040' is open on '192.168.1.99'
          Port '7680' is open on '192.168.1.99'
          Port scan finished on '192.168.1.99' in 2602ms!
```

- Precise IP scope port scan *(**WITH** dynamic ports)*
```bash
java -jar Dynamic.Network.jar scan=192.168.1.100 dport=true
```
```
Starting port scan on '192.168.1.99'... (AUTO 65535 THREADS)
          Port '135' is open on '192.168.1.99'
          Port '139' is open on '192.168.1.99'
          Port '445' is open on '192.168.1.99'
          Port '5040' is open on '192.168.1.99'
          Port '7680' is open on '192.168.1.99'
          Port '49664' is open on '192.168.1.99'
          Port '49667' is open on '192.168.1.99'
          Port '49669' is open on '192.168.1.99'
          Port '49665' is open on '192.168.1.99'
          Port '49668' is open on '192.168.1.99'
          Port '49666' is open on '192.168.1.99'
          Port scan finished on '192.168.1.99' in 3641ms!
```

##

- Advanced full network & port scan *(**WITHOUT** dynamic ports)* 
### **(WARNING: THIS IS VERY RESSOURCE HUNGRY!)**
```bash
java -jar Dynamic.Network.jar auto=true
```
```
Host '192.168.1.1' is reachable
          Starting port scan on '192.168.1.1'... (AUTO 49152 THREADS)
Host '192.168.1.99' is reachable
          Starting port scan on '192.168.1.99'... (AUTO 49152 THREADS)
Host '192.168.1.100' is reachable
          Starting port scan on '192.168.1.100'... (AUTO 49152 THREADS)
          Port '22' is open on '192.168.1.100'
          Port '135' is open on '192.168.1.99'
          Port '139' is open on '192.168.1.99'
          Port '80' is open on '192.168.1.1'
          Port '53' is open on '192.168.1.100'
          Port '53' is open on '192.168.1.1'
          Port '80' is open on '192.168.1.100'
          Port '445' is open on '192.168.1.99'
          Port '445' is open on '192.168.1.100'
Host '192.168.1.11' is reachable
          Starting port scan on '192.168.1.11'... (AUTO 49152 THREADS)
          Port '1287' is open on '192.168.1.1'
          Port '1288' is open on '192.168.1.1'
          Port '7680' is open on '192.168.1.99'
          Port '27036' is open on '192.168.1.99'
          Port scan finished on '192.168.1.11' in 23595ms!
          Port scan finished on '192.168.1.99' in 122543ms!
          Port scan finished on '192.168.1.100' in 123489ms!
          Port scan finished on '192.168.1.1' in 284563ms!
Finished network scan in 285573ms!
```

- Advanced full network & port scan *(**WITH** dynamic ports)* 
### **(WARNING: THIS IS EVEN MORE RESSOURCE HUNGRY!)**
```bash
java -jar Dynamic.Network.jar auto=true dport=true
```
```
Host '192.168.1.99' is reachable
          Starting port scan on '192.168.1.99'... (AUTO 65535 THREADS)
Host '192.168.1.1' is reachable
          Starting port scan on '192.168.1.1'... (AUTO 65535 THREADS)
Host '192.168.1.1' is reachable
          Starting port scan on '192.168.1.1'... (AUTO 65535 THREADS)
Host '192.168.1.100' is reachable
          Starting port scan on '192.168.1.100'... (AUTO 65535 THREADS)
          Port '53' is open on '192.168.1.1'
          Port '22' is open on '192.168.1.100'
          Port '53' is open on '192.168.1.1'
          Port '80' is open on '192.168.1.1'
          Port '80' is open on '192.168.1.1'
          Port '135' is open on '192.168.1.99'
          Port '139' is open on '192.168.1.99'
          Port '53' is open on '192.168.1.100'
          Port '80' is open on '192.168.1.100'
          Port '445' is open on '192.168.1.99'
          Port '445' is open on '192.168.1.100'
          Port '1287' is open on '192.168.1.1'
          Port '1288' is open on '192.168.1.1'
          Port '1287' is open on '192.168.1.1'
          Port '1288' is open on '192.168.1.1'
          Port '27036' is open on '192.168.1.99'
          Port '55469' is open on '192.168.1.99'
          Port '55458' is open on '192.168.1.99'
          Port '55470' is open on '192.168.1.99'
          Port '55457' is open on '192.168.1.99'
          Port scan finished on '192.168.1.1' in 27807ms!
          Port scan finished on '192.168.1.99' in 37819ms!
          Port scan finished on '192.168.1.1' in 64611ms!
          Port scan finished on '192.168.1.100' in 168116ms!
Finished network scan in 169737ms!
```

## Contributing

Pull requests are welcome. For major changes, please open an issue first
to discuss what you would like to change.

## License

[MIT](https://choosealicense.com/licenses/mit/)
