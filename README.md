## Requirements

Expose a REST API, where the argument is a day (yyyy-mm-dd date format) depending on which place offers best windsurfing conditions on that day in the 16 forecast day range and return value is one of following locations: 
* Jastarnia (Poland)
* Bridgetown (Barbados)
* Fortaleza (Brazil)
* Pissouri (Cyprus)
* Le Morne (Mauritius)

Apart from returning the name of the location, the response should also include weather conditions (at least average temperature - Celcius, wind speed - m/s) for the location on that day.

The best location selection criteria are: If the wind speed is not within <5; 18> (m/s) and the temperature is not in the range <5; 35> (°C), the location is not suitable for windsurfing.

If they are in these ranges, then the best location is determined by the highest value calculated from the following formula:
```json
V * 3 + TEMP
```


where V is the wind speed in m/s on a given day, and TEMP is an average forecasted temperature for a given day in Celsius

If none of the locations meets the above criteria, the application does not return any.

The list of windsurfing locations (including geographical coordinates) should be embedded in the application in a way that allows for extensions at a later stage.

## Endpoints

| endpoint          | method | request                                                               | response                        | function                                        |
|-------------------|--------|-----------------------------------------------------------------------|---------------------------------|-------------------------------------------------|
| /api/best-weather | GET    | query params: (DATE FORMAT (YYYY-MM-DD), lang (available pl and end)) | Best location based on selected | get to know where is best place for windsurfing |
    
example link: http://localhost:8080/api/best-weather?date=2024-01-26&lang=pl (make sure date is Please make sure that the date is within the next 14 days from now because thats external API requirements)
## How to build the project on your own
1. Clone this repository
```shell
git clone https://github.com/FuuKowatty/TaskManager-Backend.git
```
2. Go to the folder with cloned repository
3. Make sure you provided your properties in application.properties file (You can retrieve API_KEY for free if you create account on https://www.weatherbit.io/api)
4. Run the application