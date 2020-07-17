// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.*;
import java.util.Iterator; 


public final class FindMeetingQuery {
  private int lastEndTime;

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    ArrayList<Event> eventsList = new ArrayList(events);

    EventStartTimeComparator startTimeComparator = new EventStartTimeComparator();
    Collections.sort((List<Event>)eventsList, (Comparator<Event>) startTimeComparator);


    List<Event> eventsListCopy = new ArrayList<>(eventsList);
    Collection<TimeRange> returnTimes = findBestTimeRanges(eventsList, request, false);
    Collection<TimeRange> returnOptionalTimes = findBestTimeRanges(eventsListCopy, request, true);

    if (returnOptionalTimes.size() > 0 || request.getAttendees().size() == 0) {
        return returnOptionalTimes;
    }

    return returnTimes;
  }

  private Collection<TimeRange> findBestTimeRanges(List<Event> eventsList, MeetingRequest request, boolean includeOptional) {
    int possibleStartTime = TimeRange.START_OF_DAY;
    lastEndTime = TimeRange.START_OF_DAY;
    ArrayList<TimeRange> returnTimes = new ArrayList<>();

    while (eventsList.size() > 0) {
        Event firstEvent = findNextEvent(eventsList, request, possibleStartTime, includeOptional);
        if (firstEvent != null) {
            if (firstEvent.getWhen().start() - possibleStartTime >= request.getDuration()) {
                returnTimes.add(TimeRange.fromStartDuration(possibleStartTime, firstEvent.getWhen().start()-possibleStartTime));
                possibleStartTime = firstEvent.getWhen().end();
            } else {
                possibleStartTime = firstEvent.getWhen().end();
            }
        }

    }

    if (TimeRange.END_OF_DAY - lastEndTime >= request.getDuration()) {
        returnTimes.add(TimeRange.fromStartDuration(lastEndTime, TimeRange.END_OF_DAY - lastEndTime + 1));
    }
    return returnTimes;
  }

  /** 
  * Finds the next relevant events and stores the last end time of the latest relevant event
  */
  private Event findNextEvent(List<Event> eventsList, MeetingRequest request, int possibleStartTime, boolean includeOptional) {
    Event firstEvent = eventsList.get(0);
    // run the while loop while firstEvent starts before possibleStartTime and it is not a relevant event
    // relevant event: includeOptional or relevantEvent
    while (firstEvent.getWhen().start() < possibleStartTime || !(relevantEvent(firstEvent, request, includeOptional))) {
        if (relevantEvent(firstEvent, request, includeOptional)) {
            lastEndTime = Math.max(lastEndTime, eventsList.get(0).getWhen().end());
        }
        eventsList.remove(0);
        if (eventsList.size() > 0) {
            firstEvent = eventsList.get(0);
        } else {
            return null;
        }
    }
    return firstEvent;
  }

  private boolean relevantEvent(Event event, MeetingRequest request, boolean includeOptional) {
    Collection<String> eventAttendees = event.getAttendees();
    for (String requestAtt: request.getAttendees()) {
        if (eventAttendees.contains(requestAtt)) {
            return true;
        }
    }
    if (includeOptional) {
        for (String requestAtt: request.getOptionalAttendees()) {
            if (eventAttendees.contains(requestAtt)) {
                return true;
            }
        }
    }
    return false;
  }

  static class EventStartTimeComparator implements Comparator<Event> {
      @Override
      public int compare(Event e1, Event e2) {
          return e1.getWhen().start() - e2.getWhen().start();
      }
  }

}
