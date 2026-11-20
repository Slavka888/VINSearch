package vinsearch.logic;

import vinsearch.logic_classes.Part;

/** Result of matching a part to the car (or confirming an analog). */
public record CompatiblePart(Part part, String basis, boolean isAnalog) { }
