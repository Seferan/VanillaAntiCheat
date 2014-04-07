package net.minecraft.util;

import net.minecraft.crash.CrashReport;

public class ReportedException extends RuntimeException
{
    /** Instance of CrashReport. */
    private final CrashReport theReportedExceptionCrashReport;
    private static final String __OBFID = "CL_00001579";

    public ReportedException(CrashReport par1CrashReport)
    {
        theReportedExceptionCrashReport = par1CrashReport;
    }

    /**
     * Gets the CrashReport wrapped by this exception.
     */
    public CrashReport getCrashReport()
    {
        return theReportedExceptionCrashReport;
    }

    public Throwable getCause()
    {
        return theReportedExceptionCrashReport.getCrashCause();
    }

    public String getMessage()
    {
        return theReportedExceptionCrashReport.getDescription();
    }
}
