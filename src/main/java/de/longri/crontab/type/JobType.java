/*
 * Copyright (C) 2024 Longri
 *
 * This file is part of CrontabScheduler.
 *
 * CrontabScheduler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * CrontabScheduler is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CrontabScheduler. If not, see <https://www.gnu.org/licenses/>.
 */
package de.longri.crontab.type;


import de.longri.crontab.Job;
import org.apache.commons.cli.ParseException;
import org.reflections.Reflections;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * The JobType class represents types of jobs that can be scheduled and executed within the CrontabScheduler.
 * Each JobType has a unique name and can provide instances of jobs.
 */
public abstract class JobType {
    /**
     * A collection of all registered job types.
     */
    static final ArrayList<JobType> VALUES = new ArrayList<>();

    /**
     * A JobType representing an unknown job type.
     */
    public static final JobType UNKNOWN = new JobType("UNKNOWN") {
        @Override
        public Job getTemplate() {
            return null;
        }

        @Override
        public Job getJobInstance(String name, String args) {
            return null;
        }
    };

    /**
     * The default name for job types.
     */
    public static final String DEFAULT_NAME = "New Name";

    static {
        // register JopType UNKNOWN
        registerType(UNKNOWN);

        // Use reflections to find and register all subclasses of JobType
        Reflections reflections = new Reflections("de.longri.crontab.type");

        Set<Class<? extends JobType>> subclasses = reflections.getSubTypesOf(JobType.class);
        for (Class<? extends JobType> subclass : subclasses) {
            try {
                if (hasDefaultConstructor(subclass)) {
                    registerType(subclass.newInstance());
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * Checks if the given class has a default constructor.
     *
     * @param clazz the class to check
     * @return true if the class has a default constructor, false otherwise
     */
    private static boolean hasDefaultConstructor(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            return constructor.isAccessible() || constructor.trySetAccessible();
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Registers a new job type.
     *
     * @param type the job type to register
     */
    private static void registerType(JobType type) {
        if (type == null)
            throw new RuntimeException("JobType can't be null");
        if (VALUES.contains(type))
            throw new RuntimeException("JobType [" + type.NAME + "] is already registered");
        VALUES.add(type);
    }

    /**
     * Gets a job type from a string representation.
     *
     * @param selected the string representation of the job type
     * @return the job type
     */
    public static JobType getFromString(String selected) {
        for (JobType t : VALUES) {
            if (selected.equals(t.NAME))
                return t;
        }
        return JobType.UNKNOWN;
    }

    /**
     * Gets all registered job types as a collection of strings.
     *
     * @return a collection of job type names
     */
    public static Collection<String> StringValues() {
        ArrayList<String> list = new ArrayList<>();
        for (JobType t : VALUES) {
            list.add(t.toString());
        }
        return list;
    }
//===================================================================================================================

    private final String NAME;

    /**
     * Constructs a JobType with the given name.
     *
     * @param name the name of the job type
     */
    public JobType(String name) {
        NAME = name;
    }


    public String toString() {
        return "JobType: " + NAME;
    }

    /**
     * Gets the name of the job type in INI file format.
     *
     * @return the name of the job type
     */
    public String toIniString() {
        return NAME;
    }

    /**
     * Gets a template job for this job type.
     *
     * @return a template job
     * @throws java.text.ParseException if there is an error parsing job parameters
     * @throws GeneralSecurityException if there is a security error
     * @throws UnsupportedEncodingException if the encoding is not supported
     * @throws Exception if there is a general error
     */
    public abstract Job getTemplate() throws ParseException, GeneralSecurityException, UnsupportedEncodingException, Exception;

    /**
     * Gets a new instance of a job for this job type.
     *
     * @param name the name of the job
     * @param args the arguments for the job
     * @return a new instance of a job
     * @throws java.text.ParseException if there is an error parsing job parameters
     * @throws Exception if there is a general error
     */
    public abstract Job getJobInstance(String name, String args) throws ParseException, Exception;

    @Override
    public boolean equals(Object o) {
        if (o instanceof JobType) {
            return ((JobType) o).NAME.equals(this.NAME);
        }
        return false;
    }
}
