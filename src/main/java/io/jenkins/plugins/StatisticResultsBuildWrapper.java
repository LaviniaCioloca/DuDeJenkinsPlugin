package io.jenkins.plugins;

import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import io.jenkins.plugins.dude.model.DuDeStatisticResults;
import io.jenkins.plugins.dude.model.PluginResults;
import org.apache.commons.io.IOUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Stack;
import java.util.stream.Collectors;

import static io.jenkins.plugins.constants.ImpactValues.HIGH_IMPACT;
import static io.jenkins.plugins.constants.ImpactValues.LOW_IMPACT;
import static io.jenkins.plugins.constants.ImpactValues.MEDIUM_IMPACT;
import static io.jenkins.plugins.constants.ResultValues.*;
import static io.jenkins.plugins.constants.TrendValues.CONSTANT_SYMBOL;
import static io.jenkins.plugins.constants.TrendValues.DECREASE_ARROW_SYMBOL;
import static io.jenkins.plugins.constants.TrendValues.INCREASE_ARROW_SYMBOL;

public class StatisticResultsBuildWrapper extends BuildWrapper {

    private Double previousDuplicationPercentage;
    private Double currentDuplicationPercentage;
    private String duplicationTrend;
    private String duplicationImpact;

    @DataBoundConstructor
    public StatisticResultsBuildWrapper() {
    }

    @Override
    public Environment setUp(AbstractBuild build, final Launcher launcher, BuildListener listener) {
        return new Environment() {
            @Override
            public boolean tearDown(AbstractBuild build, BuildListener listener)
                    throws IOException, InterruptedException {
                final File artifactsDir = build.getArtifactsDir();
                if (!artifactsDir.isDirectory()) {
                    final boolean success = artifactsDir.mkdirs();
                    if (!success) {
                        listener.getLogger().println("Can't create artifacts directory at " + artifactsDir.getAbsolutePath());
                    }
                }

                final DuDeStatisticResults currentDuDeStatisticResults = buildCurrentDuDeStatisticResults(build.getWorkspace());
                PluginResults currentPluginResults;
                String reportHTML;

                if (build.getPreviousBuild() != null) {
                    final PluginResults previousPluginResults = getPreviousBuildResults(build, artifactsDir);

                    currentPluginResults = generateJSONReport(build.getProject().getDisplayName(), currentDuDeStatisticResults,
                                                              build.getId(), build.getPreviousBuild().getId());

                    reportHTML = generateHTMLReport(currentPluginResults, previousPluginResults.getPercentageOfFilesAnalysedThatHaveDuplicateFragments());
                } else {
                    currentPluginResults = generateJSONReport(build.getProject().getDisplayName(), currentDuDeStatisticResults,
                                                              build.getId(), VALUE_NOT_AVAILABLE);

                    reportHTML = generateHTMLReport(currentPluginResults, VALUE_NOT_AVAILABLE);
                }

                writePluginReports(build, artifactsDir, currentPluginResults, reportHTML);

                return super.tearDown(build, listener);
            }

            private void writePluginReports(final AbstractBuild build, final File artifactsDir, final PluginResults currentPluginResults, final String reportHTML) throws IOException {
                final String pathJSONReport = artifactsDir.getCanonicalPath() + DUDE_STATISTICS_JSON_PATH;
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathJSONReport),
                                                                                       StandardCharsets.UTF_8))) {
                    writer.write(new ObjectMapper().writeValueAsString(currentPluginResults));
                }

                final String pathHTMLReport = artifactsDir.getCanonicalPath() + DUDE_STATISTICS_HTML_PATH;
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathHTMLReport),
                                                                                       StandardCharsets.UTF_8))) {
                    writer.write(reportHTML);
                }

                final String pathHTMLReportInProjectWorkspace = build.getWorkspace() + DUDE_STATISTICS_HTML_PATH;
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathHTMLReportInProjectWorkspace),
                                                                                       StandardCharsets.UTF_8))) {
                    writer.write(reportHTML);
                }
            }

            private PluginResults getPreviousBuildResults(final AbstractBuild build, final File artifactsDir) throws IOException {
                PluginResults previousPluginResults = new PluginResults();
                final File previousArtifactsDir = build.getPreviousBuild().getArtifactsDir();

                for (final File file : previousArtifactsDir.listFiles()) {
                    if (file.getName().equals("dude-statistics.json")) {
                        previousPluginResults = new ObjectMapper().readValue(file, PluginResults.class);
                    }
                }

                final String previousPathJSONReport = artifactsDir.getCanonicalPath() + "/previous-dude-statistics.json";
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(previousPathJSONReport),
                                                                                       StandardCharsets.UTF_8))) {
                    writer.write(new ObjectMapper().writeValueAsString(previousPluginResults));
                }

                return previousPluginResults;
            }
        };
    }

    private static DuDeStatisticResults buildCurrentDuDeStatisticResults(final FilePath root) throws IOException,
                                                                                                     InterruptedException {
        DuDeStatisticResults DuDeStatisticResults = null;

        Stack<FilePath> toProcess = new Stack<>();
        toProcess.push(root);

        while (!toProcess.isEmpty()) {
            final FilePath path = toProcess.pop();
            if (path.isDirectory()) {
                toProcess.addAll(path.list());
            } else if (path.getName().equals("dude-StatisticResults.json")) {
                final InputStream statisticResultsStream = path.read();
                DuDeStatisticResults = new ObjectMapper().readValue(IOUtils.toString(statisticResultsStream), DuDeStatisticResults.class);
                break;
            }
        }

        return DuDeStatisticResults;
    }

    private static PluginResults generateJSONReport(final String projectName,
                                                    final DuDeStatisticResults currentDuDeStatisticResults,
                                                    final String currentBuildNumber, final String previousBuildNumber) {

        final PluginResults pluginResults = new PluginResults();
        pluginResults.setProjectName(projectName);
        pluginResults.setCurrentBuildNumber(currentBuildNumber);
        pluginResults.setPreviousBuildNumber(previousBuildNumber);
        pluginResults.setNumberOfFilesAnalysed(String.valueOf(currentDuDeStatisticResults.getNumberOfFilesAnalysed()));
        pluginResults.setNumberOfDuplicatedCodeFragments(String.valueOf(currentDuDeStatisticResults.getNumberOfDuplicatedCodeFragments()));
        pluginResults.setNumberOfFilesContainingDuplicateFragments(String.valueOf(currentDuDeStatisticResults.getNumberOfFilesContainingDuplicateFragments()));
        pluginResults.setFilesWithDuplicateFragments(getFilesWithDuplicateFragmentsAsString(currentDuDeStatisticResults));
        pluginResults.setPercentageOfFilesAnalysedThatHaveDuplicateFragments(String.valueOf(currentDuDeStatisticResults.getPercentageOfFilesAnalysedThatHaveDuplicateFragments()));

        // duplication fragments setters
        pluginResults.setDuplicationFragmentWithMostLOCTotalLOC(String.valueOf(currentDuDeStatisticResults.getDuplicationFragmentWithMostLOC().getDuplicationTotalLOC()));
        pluginResults.setDuplicationFragmentWithMostLOCActualLOC(String.valueOf(currentDuDeStatisticResults.getDuplicationFragmentWithMostLOC().getDuplicationActualLOC()));
        pluginResults.setDuplicationFragmentWithMostLOCFilesCount(String.valueOf(currentDuDeStatisticResults.getDuplicationFragmentWithMostLOC().getFilesHavingThisDuplicationFragmentCount()));
        pluginResults.setDuplicationFragmentWithMostLOCFiles(String.join("\n",
                                                                         currentDuDeStatisticResults.getDuplicationFragmentWithMostLOC().getFilesHavingThisDuplicationFragment()));

        pluginResults.setDuplicationFragmentPresentInMostFilesTotalLOC(String.valueOf(currentDuDeStatisticResults.getDuplicationFragmentPresentInMostFiles().getDuplicationTotalLOC()));
        pluginResults.setDuplicationFragmentPresentInMostFilesActualLOC(String.valueOf(currentDuDeStatisticResults.getDuplicationFragmentPresentInMostFiles().getDuplicationActualLOC()));
        pluginResults.setDuplicationFragmentPresentInMostFilesFilesCount(String.valueOf(currentDuDeStatisticResults.getDuplicationFragmentPresentInMostFiles().getFilesHavingThisDuplicationFragmentCount()));
        pluginResults.setDuplicationFragmentPresentInMostFilesFiles(String.join("\n",
                                                                                currentDuDeStatisticResults.getDuplicationFragmentPresentInMostFiles().getFilesHavingThisDuplicationFragment()));

        return pluginResults;
    }

    private static String generateHTMLReport(final PluginResults pluginResults,
                                             String previousPercentageOfFilesWithDuplicateFragments) throws IOException {
        final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        try (final InputStream in = StatisticResultsBuildWrapper.class.getResourceAsStream(DUDE_STATISTICS_HTML_PATH)) {
            final byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) >= 0) {
                bOut.write(buffer, 0, read);
            }
        }

        String content = new String(bOut.toByteArray(), StandardCharsets.UTF_8);
        content = content.replace(PROJECT_NAME, pluginResults.getProjectName());
        content = content.replace(CURRENT_BUILD_NUMBER, pluginResults.getCurrentBuildNumber());
        content = content.replace(PREVIOUS_BUILD_NUMBER, pluginResults.getPreviousBuildNumber());
        content = content.replace(NUMBER_OF_FILES_ANALYSED, pluginResults.getNumberOfFilesAnalysed());
        content = content.replace(NUMBER_OF_DUPLICATED_CODE_FRAGMENTS, pluginResults.getNumberOfDuplicatedCodeFragments());
        content = content.replace(NUMBER_OF_FILES_CONTAINING_DUPLICATE_FRAGMENTS,
                                  pluginResults.getNumberOfFilesContainingDuplicateFragments());
        content = content.replace(FILES_WITH_DUPLICATE_FRAGMENTS, pluginResults.getFilesWithDuplicateFragments());
        content = content.replace(PERCENTAGE_OF_FILES_ANALYSED_THAT_HAVE_DUPLICATE_FRAGMENTS,
                                  pluginResults.getPercentageOfFilesAnalysedThatHaveDuplicateFragments());
        content = content.replace(PREVIOUS_PERCENTAGE_OF_FILES_ANALYSED_THAT_HAVE_DUPLICATE_FRAGMENTS,
                                  previousPercentageOfFilesWithDuplicateFragments);

        // duplication fragment with most LOC
        content = content.replace(DUPLICATION_FRAGMENT_WITH_MOST_LOC_TOTAL_LOC,
                                  pluginResults.getDuplicationFragmentWithMostLOCTotalLOC());
        content = content.replace(DUPLICATION_FRAGMENT_WITH_MOST_LOC_ACTUAL_LOC,
                                  pluginResults.getDuplicationFragmentWithMostLOCActualLOC());
        content = content.replace(DUPLICATION_FRAGMENT_WITH_MOST_LOC_FILES_COUNT,
                                  pluginResults.getDuplicationFragmentWithMostLOCFilesCount());
        content = content.replace(DUPLICATION_FRAGMENT_WITH_MOST_LOC_FILES,
                                  pluginResults.getDuplicationFragmentWithMostLOCFiles());

        // duplication fragment present in most files
        content = content.replace(DUPLICATION_FRAGMENT_PRESENT_IN_MOST_FILES_TOTAL_LOC,
                                  pluginResults.getDuplicationFragmentPresentInMostFilesTotalLOC());
        content = content.replace(DUPLICATION_FRAGMENT_PRESENT_IN_MOST_FILES_ACTUAL_LOC,
                                  pluginResults.getDuplicationFragmentPresentInMostFilesActualLOC());
        content = content.replace(DUPLICATION_FRAGMENT_PRESENT_IN_MOST_FILES_FILES_COUNT,
                                  pluginResults.getDuplicationFragmentPresentInMostFilesFilesCount());
        content = content.replace(DUPLICATION_FRAGMENT_PRESENT_IN_MOST_FILES_FILES,
                                  pluginResults.getDuplicationFragmentPresentInMostFilesFiles());

        if (previousPercentageOfFilesWithDuplicateFragments.equals(VALUE_NOT_AVAILABLE)) {
            content = generateTrendAndImpactValuesWithoutPreviousBuild(content,
                                                                       pluginResults.getPercentageOfFilesAnalysedThatHaveDuplicateFragments());
        } else {
            content = generateTrendAndImpactValues(content,
                                                   pluginResults.getPercentageOfFilesAnalysedThatHaveDuplicateFragments(),
                                                   previousPercentageOfFilesWithDuplicateFragments);
        }

        return content;
    }

    private static String generateTrendAndImpactValuesWithoutPreviousBuild(String content, final String currentDuplicationPercentage) {
        content = content.replace(DUPLICATION_TREND, CONSTANT_SYMBOL);

        content = generateImpactValues(content, currentDuplicationPercentage);

        return content;
    }

    private static String generateTrendAndImpactValues(String content,
                                                       final String currentDuplicationPercentage,
                                                       final String previousDuplicationPercentage) {
        final Double duplicationPercentageChange =
                Double.valueOf(currentDuplicationPercentage) - Double.valueOf(previousDuplicationPercentage);

        if (duplicationPercentageChange == 0) {
            content = content.replace(DUPLICATION_TREND, CONSTANT_SYMBOL);
        } else if (duplicationPercentageChange < 0) {
            content = content.replace(DUPLICATION_TREND, DECREASE_ARROW_SYMBOL);
        } else {
            content = content.replace(DUPLICATION_TREND, INCREASE_ARROW_SYMBOL);
        }

        content = generateImpactValues(content, currentDuplicationPercentage);

        return content;
    }

    private static String generateImpactValues(String content, final String currentDuplicationPercentage) {
        if (Double.valueOf(currentDuplicationPercentage) >= 20) {
            content = content.replace(DUPLICATION_IMPACT, HIGH_IMPACT);
        } else if (Double.valueOf(currentDuplicationPercentage) >= 10 && Double.valueOf(currentDuplicationPercentage) < 20) {
            content = content.replace(DUPLICATION_IMPACT, MEDIUM_IMPACT);
        } else {
            content = content.replace(DUPLICATION_IMPACT, LOW_IMPACT);
        }

        return content;
    }

    private static String getFilesWithDuplicateFragmentsAsString(final DuDeStatisticResults DuDeStatisticResults) {
        return DuDeStatisticResults.getFilesWithDuplicateFragments()
                                   .stream()
                                   .map(Object::toString)
                                   .collect(Collectors.joining("\n"));
    }

    @Extension
    public static final class DescriptorImpl extends BuildWrapperDescriptor {

        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Construct DuDe duplication analysis during build";
        }

    }

}
