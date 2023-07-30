package com.busfare.data.io;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.busfare.calculate.*;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.enums.CSVReaderNullFieldIndicator;

public class FileReadWrite {

	/**
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static List<String[]> readFile() throws IOException {
		Reader reader = Files.newBufferedReader(Paths.get("src/main/resources/inputTaps.csv"));

		CSVReader csvReader = new CSVReaderBuilder(reader).withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
				// to skip the header
				.withSkipLines(1)
				.build();
		// to read all the rows in the file at once
		return csvReader.readAll();
	}

	/**
	 * 
	 * @param allRows
	 * @return
	 */
	public static List<TapOnOff> processData(List<String[]> allRows) {
		List<TapOnOff> tapOnOffList = new ArrayList<>(); 
		// Read CSV file line by line,  set the input TapOnOff object using the string array
		for (String[] row : allRows) {

			TapOnOff tapOnOff = new TapOnOff();
			tapOnOff.setId(row[0]);
			tapOnOff.setDateTime(formatDateTime(row[1]));
			tapOnOff.setTapType(row[2]);
			tapOnOff.setStopId(row[3]);
			tapOnOff.setCompanyId(row[4]);
			tapOnOff.setBusId(row[5]);
			tapOnOff.setPanNo(row[6]);

			tapOnOffList.add(tapOnOff);
		}
		return tapOnOffList;
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	private static Date formatDateTime(String date) {
		//System.out.println("csv file date:: " + date);
		final SimpleDateFormat dateFormat =  new SimpleDateFormat("dd-mm-YYYY HH:mm");
		Date formattedDate = null;
		try {
			formattedDate = dateFormat.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return formattedDate;
	}

	/**
	 * 
	 * @param customersList
	 */
	public static void writeToFile(List<TripResult> tripResult) {

		String[] CSV_HEADER = { "Started", "Finished", "DurationInMinutes","FromStopId", 
				"ToStopId","TripFare","CompanyId","BusId","PAN","Status" };

		try (
				Writer writer = Files.newBufferedWriter(Paths.get("src/main/resources/tripOutput.csv"));	

				CSVWriter csvWriter = new CSVWriter(writer,
						CSVWriter.DEFAULT_SEPARATOR,
						CSVWriter.NO_QUOTE_CHARACTER,
						CSVWriter.DEFAULT_ESCAPE_CHARACTER,
						CSVWriter.DEFAULT_LINE_END);
				)
		{			
			csvWriter.writeNext(CSV_HEADER);

			for (TripResult ride : tripResult) {
				String[] output = {
						ride.getStartDate().toString(),
						ride.getEndDate().toString(),
						ride.getDuration().toString(), ride.getFromStopId(), ride.getEndStopId(),
						String.valueOf(ride.getTripFare()), ride.getCompanyId(), ride.getBusId(),
						ride.getPanNo(), ride.getStatus().toUpperCase()
				};

				csvWriter.writeNext(output);
			}			
			System.out.println("Write to CSV file using CSVWriter successfully");
		}catch (Exception e) {
			System.out.println("Writing to CSV file has error");
			e.printStackTrace();
		}
	}
}
