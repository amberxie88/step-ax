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
    ArrayList<TimeRange> returnTimes = new ArrayList<>();
    int possibleStartTime = TimeRange.START_OF_DAY;
    lastEndTime = TimeRange.START_OF_DAY;
    ArrayList<Event> eventsList = new ArrayList(events);

    EventStartTimeComparator startTimeComparator = new EventStartTimeComparator();
    Collections.sort((List<Event>)eventsList, (Comparator<Event>) startTimeComparator);

    while (eventsList.size() > 0) {
        Event firstEvent = findNextEvent(eventsList, request, possibleStartTime);

        if (firstEvent != null) {
            lastEndTime = eventsList.get(0).getWhen().end();
            if (firstEvent.getWhen().start() - possibleStartTime >= request.getDuration()) {
                returnTimes.add(TimeRange.fromStartDuration(possibleStartTime, firstEvent.getWhen().start()-possibleStartTime));
                TimeRange tr = TimeRange.fromStartDuration(possibleStartTime, firstEvent.getWhen().start()-possibleStartTime);
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

  private Event findNextEvent(List<Event> eventsList, MeetingRequest request, int possibleStartTime) {
    Event firstEvent = eventsList.get(0);
    while (firstEvent.getWhen().start() < possibleStartTime || !relevantEvent(firstEvent, request)) {
        if (relevantEvent(firstEvent, request)) {
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

  private boolean relevantEvent(Event event, MeetingRequest request) {
    Collection<String> eventAttendees = event.getAttendees();
    for (String requestAtt: request.getAttendees()) {
        if (eventAttendees.contains(requestAtt)) {
            return true;
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
