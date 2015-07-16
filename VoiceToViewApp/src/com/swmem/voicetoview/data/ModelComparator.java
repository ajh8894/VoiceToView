package com.swmem.voicetoview.data;

import java.util.Comparator;

public class ModelComparator implements Comparator<Model> {
	@Override
	public int compare(Model m1, Model m2) {
		return m1.getMessageNum() - m2.getMessageNum();
	}
}
