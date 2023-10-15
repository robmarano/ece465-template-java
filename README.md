# README.md

## Wireshark listening in on network traffic
For development, use ```localhost``` or ```127.0.0.1``` as the node host name or IP address. Us the following filter for WireShark, where 1971 is an example of the control plane port and 1972 is an example of the data plane port serviced by the node "server":
```bash
tcp.port == 1971 || tcp.port == 1972
```