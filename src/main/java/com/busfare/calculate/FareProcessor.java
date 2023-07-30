package com.busfare.calculate;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.busfare.data.io.FileReadWrite;

@Service
public class FareProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(FareProcessor.class);

	@SuppressWarnings("removal")
	public void readTaps() {

		List<TapOnOff> tapOnOffList = new ArrayList<>();		
		List<TripResult> tripResultList = new ArrayList<>();
		LOGGER.debug("Loading input taps.csv file");
		try 
		{
			List<String[]> allRows = FileReadWrite.readFile();
			if(allRows != null && !allRows.isEmpty()) {
				tapOnOffList = FileReadWrite.processData(allRows);
			}

			//sort the list based on dateTime parameter
			Collections.sort(tapOnOffList, new Comparator<TapOnOff>() {
				public int compare(final TapOnOff obj1, final TapOnOff obj2) {
					return obj1.getDateTime().compareTo(obj2.getDateTime());
				}
			});

			//calculate fare per ride
			if(tapOnOffList != null && !tapOnOffList.isEmpty()) 
			{
				for (TapOnOff tapOnOff : tapOnOffList) 
				{					
					TripResult tripResult = new TripResult();
					String panNo = tapOnOff.getPanNo().trim();
					String tapType = tapOnOff.getTapType().trim();
					Date tapOnTime = tapOnOff.getDateTime();
					String busId = tapOnOff.getBusId();

					if("ON".equalsIgnoreCase(tapType) ) 
					{
						boolean matchingFound = false;
						for (TapOnOff tapOnOffNext : tapOnOffList) 
						{	
							//Business logic to match the records of the customer based on PAN,
							//BUS Id, Time of Travel and Duration of travel
							if(tapOnOffNext.getPanNo().trim().equals(panNo) 
									&& tapOnOffNext.getBusId().equals(busId)
									&& "OFF".equals(tapOnOffNext.getTapType().trim())
									&& (tapOnOffNext.getDateTime().compareTo(tapOnTime)) > 0 
									&& isTimeDifferenceWithinRange(tapOnOffNext.getDateTime(), tapOnOffNext.getDateTime())) 
							{		
								tripResult = storeTripData(tapOnOffNext, tapOnOff);
								// trip status check			
								if(!tapOnOff.getStopId().equalsIgnoreCase(tapOnOffNext.getStopId())) {
									tripResult.setStatus("COMPLETED");
									// fetch the standard set fare									
									Double fare = getFare(tapOnOff.getStopId(), tapOnOffNext.getStopId());
									tripResult.setTripFare(fare);
								}
								else {
									tripResult.setStatus("CANCELLED");
									tripResult.setTripFare(new Double(0));
								}
								matchingFound = true;
								tripResultList.add(tripResult);
								break;
							}//end of inner if
						}//end of inner for loop
						if (!matchingFound)
						{
							//incomplete trip, when there is no Tap 'Off' recorded
							tripResult.setStartDate(tapOnTime);
							tripResult.setEndDate(tapOnOff.getDateTime());													
							tripResult.setDuration(Long.parseLong("0"));								
							tripResult.setFromStopId(tapOnOff.getStopId());
							tripResult.setEndStopId(tapOnOff.getStopId());
							tripResult.setCompanyId(tapOnOff.getCompanyId());
							tripResult.setBusId(tapOnOff.getBusId());
							tripResult.setPanNo(panNo);
							tripResult.setStatus("INCOMPLETE");
							// max fare calculation									
							tripResult.setTripFare(getMaxFare(tapOnOff.getStopId()));						
							tripResultList.add(tripResult);
						}
					}// end of first if  tap type "ON" loop
				}//end of first for loop

			}//end of list if loop
			//write output to CSV file		
			FileReadWrite.writeToFile(tripResultList);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private boolean isTimeDifferenceWithinRange (Date startDate, Date endDate) {
		LocalDateTime initialTime = LocalDateTime.ofInstant(startDate.toInstant(),
				ZoneId.systemDefault());
		LocalDateTime FinalTime = LocalDateTime.ofInstant(endDate.toInstant(),
				ZoneId.systemDefault());
		long difference = Duration.between(initialTime, FinalTime).getSeconds();
		if(difference <= 1200) {
			return true;
		}
		return false;
	}

	private TripResult storeTripData(TapOnOff tapOnNext, TapOnOff tapOnOff)
	{
		TripResult tripResult = new TripResult();
		Date startDate = tapOnOff.getDateTime();
		Date finishDate = tapOnNext.getDateTime();

		tripResult.setStartDate(startDate);
		tripResult.setEndDate(finishDate);
		long secondsBetween = (finishDate.getTime() - startDate.getTime()) / 1000;								
		tripResult.setDuration(secondsBetween/60);
		tripResult.setFromStopId(tapOnOff.getStopId());
		tripResult.setEndStopId(tapOnNext.getStopId());
		tripResult.setCompanyId(tapOnNext.getCompanyId());
		tripResult.setBusId(tapOnNext.getBusId());
		tripResult.setPanNo(tapOnOff.getPanNo());
		return tripResult;
	}

	/**
	 * 
	 * @param stop1
	 * @param stop2
	 * @return
	 */
	private Double getFare(String beginStop, String endStop)
	{
		Double fare = 0.0;
		for(Fares fares : Fares.values())
		{
			if(beginStop.equalsIgnoreCase(fares.getBeginStop()) && endStop.equalsIgnoreCase(fares.getEndStop()))
			{
				fare = Double.parseDouble(fares.getFare()); 
			}
		}
		return fare;
	}

	/**
	 * 
	 * @param stopId
	 * @return
	 */
	private Double getMaxFare(String stopId)
	{
		Double fare = 0.0;
		List<Double> fareList = new ArrayList<>();

		for(Fares fares : Fares.values())
		{
			if(stopId.equalsIgnoreCase(fares.getBeginStop())) 
				fareList.add(Double.parseDouble(fares.getFare()));
		}
		fare = Collections.max(fareList);
		return fare;
	}

}
