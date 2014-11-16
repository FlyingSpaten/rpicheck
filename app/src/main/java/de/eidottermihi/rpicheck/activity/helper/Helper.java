package de.eidottermihi.rpicheck.activity.helper;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Utility class for conversion, formatting, etc.
 * 
 * @author Michael
 * 
 */
public class Helper {
	public static final String SCALE_CELSIUS = "°C";
	public static final String SCALE_FAHRENHEIT = "°F";

	public static final String SCALE_HZ = "Hz";
	public static final String SCALE_MHZ = "MHz";
	public static final String SCALE_GHZ = "GHz";

	private static final String N_A = " n/a ";

	private static final NumberFormat decimalFormat = DecimalFormat
			.getNumberInstance();

	/**
	 * Formats a temperature in celsius.
	 * 
	 * @param tempInCelsius
	 *            the temperature to be formatted (in celsius)
	 * @param tempScale
	 *            the scale to use (°C/°F).
	 * @return the formatted temperature
	 */
	public String formatTemperature(double tempInCelsius, String tempScale) {
		if (tempScale.equals(SCALE_CELSIUS)) {
			return decimalFormat.format(tempInCelsius) + SCALE_CELSIUS;
		} else {
			return decimalFormat.format(celsiusToFahrenheit(tempInCelsius))
					+ SCALE_FAHRENHEIT;
		}
	}

	private double celsiusToFahrenheit(double celsius) {
		return celsius * 1.8 + 32;
	}

	/**
	 * Formats a frequency in Hz.
	 * 
	 * @param frequencyInHz
	 *            the frequency in Hz
	 * @param scale
	 *            the scale (Hz/MHz/GHz)
	 * @return the formatted frequency
	 */
	public String formatFrequency(long frequencyInHz, String scale) {
		if (scale.equals(SCALE_HZ)) {
			return decimalFormat.format(frequencyInHz) + " " + scale;
		} else if (scale.equals(SCALE_GHZ)) {
			final String output = decimalFormat.format(new BigDecimal(
					frequencyInHz).divide(new BigDecimal(1000000000))
					.doubleValue())
					+ " " + scale;
			return output;
		} else {
			return decimalFormat.format(frequencyInHz / 1000000) + " " + scale;
		}
	}

	/**
	 * Formats a Number.
	 * 
	 * @param number
	 *            the number
	 * @param maxFractionDigits
	 *            max fraction digits
	 * @return the formatted number
	 */
	public String formatDecimal(double number) {
		return decimalFormat.format(number);
	}

	/**
	 * Formats a percentage value (0-100).
	 * 
	 * @param percentage
	 *            the percentage value (0-100).
	 * @return formatted string "[percentage] %".
	 */
	public String formatPercentage(Integer percentage) {
		if (percentage != null) {
			return percentage + " %";
		} else {
			return N_A;
		}
	}

	/**
	 * Formats the wifi-signal. If the number is negative, it is possibly a dBm
	 * value.
	 * 
	 * @param signal
	 *            the signal (may be null)
	 * @return the formatted signal
	 */
	public String formatWifiSignale(Integer signal) {
		if (signal != null) {
			if (signal > 0) {
				// percent value
				return formatPercentage(signal);
			} else {
				// dBm value
				return formatDbmValue(signal);
			}
		} else {
			return N_A;
		}
	}

	/**
	 * Formats a dBm value.
	 * 
	 * @param signal
	 *            the wifi signal in dBm
	 * @return the formatted dBm signal
	 */
	public String formatDbmValue(Integer signal) {
		if (signal != null) {
			return signal + " dBm";
		} else {
			return N_A;
		}
	}

}
