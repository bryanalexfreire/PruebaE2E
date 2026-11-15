package com.screenplay.project.util.evidence;

/**
 * A report that receives the evidence recorded by the steps.
 * Each report keeps only what it is for, so nothing is stored twice:
 * Serenity holds the full evidence (screenshots per step, page HTML at a failure),
 * Cucumber holds the results and the data as text.
 */
public interface EvidenceSink {

    /** Records a titled block of text (values read from the app, failure context). */
    void text(String title, String content);
}
