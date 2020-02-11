// ******************************************************************************
//  Copyright (c) 2019 IBM Corporation and others.
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  which accompanies this distribution, and is available at
//  http://www.eclipse.org/legal/epl-v10.html
//
//  Contributors:
//  IBM Corporation - initial API and implementation
// ******************************************************************************

package io.openliberty.graphql.sample;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.GraphQLException;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
public class WeatherService {

    Map<String, Conditions> currentConditionsMap = new HashMap<>();

    @Query
    public Conditions currentConditions(@Name("location") String location) throws UnknownLocationException {
        if ("nowhere".equalsIgnoreCase(location)) {
            throw new UnknownLocationException(location);
        }
        return currentConditionsMap.computeIfAbsent(location, this::randomWeatherConditions);
    }

    @Query
    public List<Conditions> currentConditionsList(@Name("locations") List<String> locations)
        throws UnknownLocationException, GraphQLException {

        List<Conditions> allConditions = new LinkedList<>();
        for (String location : locations) {
            try {
                allConditions.add(currentConditions(location));
            } catch (UnknownLocationException ule) {
                throw new GraphQLException(ule, allConditions);
            }
        }
        return allConditions;
    }

    private Conditions randomWeatherConditions(String location) {
        Conditions c = new Conditions(location);
        c.setDayTime(Math.random() > 0.5);
        c.setTemperatureF(Math.random() * 100);
        c.setTemperatureC( (c.getTemperatureF() - 30) / 2 );
        c.setHasPrecipitation(Math.random() > 0.7);
        c.setPrecipitationType(c.isHasPrecipitation() ? PrecipType.fromTempF(c.getTemperatureF()) : null);
        c.setWeatherText(c.isHasPrecipitation() ? "Overcast" : "Sunny");
        return c;
    }
}
