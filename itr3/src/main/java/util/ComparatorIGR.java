package util;

import java.util.Comparator;

public class ComparatorIGR implements Comparator{

	@Override
	public int compare(Object o1, Object o2) {
		IGRperFeature igr1=(IGRperFeature) o1;
		IGRperFeature igr2=(IGRperFeature) o2;
		if(igr1.getIGR()<=igr2.getIGR()){
			return 1;
		}
		else{
			return -1;
		}
	}

}
