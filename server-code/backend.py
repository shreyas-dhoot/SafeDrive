import socket
import open_api_parser 
import classifier_lib
import call_here
def get_value_to_send(packet):
    splited = packet.split('#')
    if (len(splited) == 5):
        lat,longi, speed, vehicle, address = splited
        weather_code = open_api_parser.get_weather_code(lat, longi)
        #write for jamming
        address = " ".join(address.split(',')[:-3])
        jf, loc = call_here.most_match_jf(address, lat, longi)
        print(address)
        print(loc)
        #call ml algorithm
        print(lat, longi, speed, vehicle, weather_code)
        #now dummy return
    else:
        speed, vehicle, jf, weather_code = splited
    return classifier_lib.check_single_point(speed, vehicle, jf, weather_code)


UDP_PORT = 5006
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.bind(('',UDP_PORT))

while True:
    data, addr = sock.recvfrom(1024)
    recived_packet = data.decode('UTF-8')
    val = get_value_to_send(recived_packet)
    print(bytes(str(val).encode('UTF-8')))
    sock.sendto(bytes(str(val).encode('UTF-8')), addr)
