package kakao.util;

import java.util.HashMap; // similar to map in C++
import java.util.ArrayList;	// similar to vector in C++
import java.util.StringTokenizer;

public class Cmdline {

	protected HashMap<String, String> help;
	protected HashMap<String, String> value;
	public String delimiter;
	
	public Cmdline(int argc, String[] argv) throws Exception {
		delimiter = ";,";
		int i = 1;
		while (i < argc) {
			String s = argv[i];
			if (parse_name(s)) {
				if (value.containsKey(s)) {
					throw new Exception("the parameter " + s + " is already specified");
				}
				if ((i+1) < argc) {
					String s_next = argv[i+1];
					if (!parse_name(s_next)) {
						value.put(s, s_next);
						i++;
					} else {
						value.put(s, "");
					}
				} else {
					value.put(s, "");
				}
			} else {
				throw new Exception("cannot parse " + s);
			}
			i++;
		}
	}
	
	protected boolean parse_name(String s) {
		if ((s.length() > 0) && (s.charAt(0) == '-')) {
			if ((s.length() > 1) && (s.charAt(1) == '-')) {
				s = s.substring(2);
			} else {
				s = s.substring(1);
			}
			return true;
		} else {
			return false;
		}
	}
	
	public void setValue(String parameter, String value) {
		this.value.put(parameter, value);
	}
	
	public boolean hasParameter(String parameter) {
		return value.containsKey(parameter);
	}
	
	public void print_help() {
		for (String key : help.keySet()) {
			System.out.print("-" + key);
			for (int i = key.length()+1; i < 16; i++) { System.out.print(" "); }
			String s_out = help.get(key);
			while (s_out.length() > 0) {
				if (s_out.length() > (72-16)) {
					int p = s_out.substring(0, 72-16).lastIndexOf(" \t");
					if (p == 0) {
						p = 72-16;
					}
					System.out.println(s_out.substring(0,p));
					s_out = s_out.substring(p+1,s_out.length()-p);
				} else {
					System.out.println(s_out);
				}
				if (s_out.length() > 0) {
					for (int i = 0; i < 16; i++) { System.out.print(" "); }
				}
			}
		}
	}
	
	public String registerParameter(String parameter, String help) {
		this.help.put(parameter, help);
		return parameter;
	}
	
	public void checkParameters() throws Exception {
		// make sure there is no parameter specified on the cmdline that is not registered:
		for (String key : help.keySet()) {
			if (value.containsKey(key)) {
				throw new Exception("the parameter " + key + " does not exist");
			}
		}
	}
	
	public String getValue(String parameter) {
		return value.get(parameter);
	}
	
	public String getValue(String parameter, String default_value) {
		if (hasParameter(parameter)) {
			return value.get(parameter);
		} else {
			return default_value;
		}
	}
	
	public double getValue(String parameter, double default_value) {
		if (hasParameter(parameter)) {
			return Double.parseDouble(value.get(parameter));
		} else {
			return default_value;
		}
	}
	
	public int getValue(String parameter, int default_value) {
		if (hasParameter(parameter)) {
			return Integer.parseInt(value.get(parameter));
		} else {
			return default_value;
		}
	}
	
	public ArrayList<String> getStrValues(String parameter) {
		ArrayList<String> result = new ArrayList<String>();
		StringTokenizer tokens = new StringTokenizer(value.get(parameter), delimiter);
		while (tokens.hasMoreTokens()) {
			result.add(tokens.nextToken());
		}
		return result;
	}
	
	// public ArrayList<Integer> getIntValues(String parameter) {}

	// public ArrayList<Double> getDblValues(String parameter) {}

} /* Kilho has finished it */


















