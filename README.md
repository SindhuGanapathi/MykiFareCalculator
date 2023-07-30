# MykiFareCalculator - Manage myki travels along with fare information for customer trips

**PROJECT DESCRIPTION:**

Passenger taps the credit card (with associated PAN) while boarding the bus and similarly expected to 
tap off while alighting. Based on the number of stops traveled by the passenger, bus fare is calculated.

**BUS FARE calculation in ideal hop on offs goes as follows:**

Stop1 to Stop2 or vice versa, travel costs **$3.25**

Stop2 to Stop3 or vice versa, travel costs **$5.50**

Stop1 to Stop3 or the vice versa, travel costs **$7.30**

These are ideal scenarios where the passenger taps on and off correctly and the trip status is considered to be **COMPLETED**

When a passenger taps on at one of the Stops and immediately taps off, the trip costs $0 and trip status is **CANCELLED** 

When the passenger taps on while boarding but misses to tap off his card while getting off the bus, in which case
maximum fare is calculated between the stops and status is considered as **INCOMPLETE**
**Example:** Passenger has traveled from Stop1 to Stop2 but has missed to tap off the card at stop2, in which case
          trip is considered 'INCOMPLETE' with a fare of $7.30 (charged for the maximum stops)

**ASSUMPTIONS:**

Stops are at equi-distant, it takes 10 minutes from Stop1 to Stop2 and similarly it takes 10 minutes to reach from Stop2 to Stop3. 

Stop2 being the midpoint of the travel.

**This leads to the assumption that user can reach the farthest stop, Stop1 to Stop3 and vice versa in 20 minutes.**

This business logic has been considered to calculate travel output details. 

Also, the start dateTime and end dateTime of trips in the input CSV file needs to be entered accordingly while testing.



**IMPLEMENTATION:**
**Step1:** Read the CSV file containing the tap on/off information for the customer : 
       "ID", "DateTimeUTC", "TapType",	"StopId", "CompanyId", "BusID", "PAN"
       
**Step2:** Process the information as per the details and calculate the applicable fares.

**Step3:** Write the processed information containing the following details to the output CSV file: 
       "Started", "Finished", "DurationInMinutes", "FromStopId", "ToStopId", "TripFare", "CompanyId", "BusId", "PAN", "Status"




**Technology Stack and installations required:**

Java8

SpringBoot

openCSV for CSV file read/write operations
