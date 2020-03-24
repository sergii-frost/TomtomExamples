We are expecting data coming from API to be reflected in corresponding Tomtom SDK data models. 
But in some cases we are not able to access all the data coming from API within the tomtom data models. 

Amongst examples are `roadNumbers` and `exitNumber` <-- they are always null in the model despite data is present in API response. 

Let's take a look at the example below: 

API call looks like this: https://api.tomtom.com/routing/1/calculateRoute/59.3316997,18.0289504:59.33204651,18.02907181:59.36326218,18.0198555/json?instructionsType=text&language=sv&vehicleHeading=344&traffic=true&travelMode=taxi&key=

API Response includes instructions which lead alongside highway `E4` and ask to follow the exit `167` from E4 towards Frösundaleden to Solna.

Here is what we get in API response for these 2 instructions: 

![Tomtom Api json example]()

At same time when trying to access the same instructions as data models we can see that `roadNumbers` in instruction[14] and `exitNumber` in instruction[15] are both null, even though there are present in API response:

![Tomtom Api data model example]()