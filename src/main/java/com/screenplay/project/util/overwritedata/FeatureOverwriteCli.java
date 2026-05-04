package com.screenplay.project.util.overwritedata;

import java.io.File;
import java.util.List;

/**
 * Command-line entry point for the feature-overwrite pipeline.
 *
 * <p>Accepts one optional argument:</p>
 * <ul>
 *   <li>{@code prepare} (default) — materializes external data into feature files.</li>
 *   <li>{@code restore} — restores the original @externaldata descriptors.</li>
 * </ul>
 *
 * <p>This class is called by the Gradle tasks
 * {@code prepareExternalDataFeatures} and {@code restoreExternalDataFeatures}.</p>
 */
public final class FeatureOverwriteCli {

    /** Root directory where all Cucumber feature files are stored. */
    private static final String FEATURES_PATH = "src/test/resources/features";

    /** File extension used to identify Cucumber feature files. */
    private static final String EXT_FEATURE = ".feature";

    private FeatureOverwriteCli() {
    }

    public static void main(String[] args) throws Exception {
        String mode = args.length > 0 ? args[0] : "prepare";
        List<String> features = FeatureOverwrite.listFilesByFolder(new File(FEATURES_PATH));

        try {
            for (String feature : features) {
                if (feature.contains(EXT_FEATURE)) {
                    if ("restore".equalsIgnoreCase(mode)) {
                        FeatureOverwrite.restoreFeatureFiles(feature);
                    } else {
                        FeatureOverwrite.overwriteFeatureFileAdd(feature);
                    }
                }
            }
        } finally {
            FeatureOverwrite.clearListFilesByFolder();
        }
    }
}

