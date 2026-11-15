package model;

public record Road(String from, String to, double distance, double speedLimit) {

  public Road {
    from = validateId(from, "Starting intersection");
    to = validateId(to, "Destination intersection");

    if (from.equals(to)) {
      throw new IllegalArgumentException("A road cannot connect an intersection to itself");
    }

    if (!Double.isFinite(distance) || distance <= 0) {
      throw new IllegalArgumentException("Distance must be a finite positive number");
    }

    if (!Double.isFinite(speedLimit) || speedLimit <= 0) {
      throw new IllegalArgumentException("Speed limit must be a finite positive number");
    }
  }

  private static String validateId(String id, String fieldName) {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException(fieldName + " cannot be null or blank");
    }
    return id.strip();
  }

  /** Returns the estimated travel time in minutes, assuming travel at the posted speed limit. */
  public double estimatedTravelTime() {
    return (distance / speedLimit) * 60.0;
  }
}
