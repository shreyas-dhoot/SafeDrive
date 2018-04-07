import datetime
import json
import urllib
import urllib.request
def url_builder(lat, longi):
    user_api = '8da6bb3dd17ed560779d74a0ca72884b'  # Obtain yours form: http://openweathermap.org/
    unit = 'metric'  # For Fahrenheit use imperial, for Celsius use metric, and the default is Kelvin.
    api_till_lat = 'http://api.openweathermap.org/data/2.5/weather?lat='     # Search for your city ID here: http://bulk.openweathermap.org/sample/city.list.json.gz
    longitude_string = '&lon='
    full_api_url = api_till_lat + str(lat) + longitude_string + str(longi)  + '&mode=json&units=' + unit + '&APPID=' + user_api
    return full_api_url

def data_fetch(full_api_url):
    url = urllib.request.urlopen(full_api_url)
    output = url.read().decode('utf-8')
    raw_api_dict = json.loads(output)
    url.close()
    weather_code = raw_api_dict.get('weather')[0]['id']
    attribute = 0
    if(weather_code>=200 and weather_code < 300):
        attribute = 0
    elif(weather_code>=300 and weather_code < 800):
        attribute = 1
    else:
        attribute = 2
    return attribute

def get_weather_code(lat, longi):
    return data_fetch(url_builder(lat, longi))
