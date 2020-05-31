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

public class StatisticResultsBuildWrapper extends BuildWrapper {

    private static final String DUDE_STATISTICS_HTML_PATH = "/dude-statistics.html";
    private static final String DUDE_STATISTICS_JSON_PATH = "/dude-statistics.json";
    private static final String PROJECT_NAME = "$PROJECT_NAME$";
    private static final String CURRENT_BUILD_NUMBER = "$CURRENT_BUILD_NUMBER$";
    private static final String PREVIOUS_BUILD_NUMBER = "$PREVIOUS_BUILD_NUMBER$";
    private static final String NUMBER_OF_FILES_ANALYSED = "$NUMBER_OF_FILES_ANALYSED$";
    private static final String NUMBER_OF_DUPLICATED_CODE_FRAGMENTS = "$NUMBER_OF_DUPLICATED_CODE_FRAGMENTS$";
    private static final String NUMBER_OF_FILES_CONTAINING_DUPLICATE_FRAGMENTS = "$NUMBER_OF_FILES_CONTAINING_DUPLICATE_FRAGMENTS$";
    private static final String FILES_WITH_DUPLICATE_FRAGMENTS = "$FILES_WITH_DUPLICATE_FRAGMENTS$";
    private static final String PERCENTAGE_OF_FILES_ANALYSED_THAT_HAVE_DUPLICATE_FRAGMENTS =
            "$PERCENTAGE_OF_FILES_ANALYSED_THAT_HAVE_DUPLICATE_FRAGMENTS$";

    @DataBoundConstructor
    public StatisticResultsBuildWrapper() {
    }

    @Override
    public Environment setUp(AbstractBuild build, final Launcher launcher, BuildListener listener) {
        return new Environment() {
            @Override
            public boolean tearDown(AbstractBuild build, BuildListener listener)
                    throws IOException, InterruptedException {
                final DuDeStatisticResults currentDuDeDuDeStatisticResults =
                        buildCurrentDuDeStatisticResults(build.getWorkspace());
                final PluginResults pluginResults = generateJSONReport(build.getProject().getDisplayName(), currentDuDeDuDeStatisticResults,
                                                                       build.getId(), build.getPreviousBuild().getId());
                final String reportHTML = generateHTMLReport(pluginResults);
                final File artifactsDir = build.getArtifactsDir();
                if (!artifactsDir.isDirectory()) {
                    final boolean success = artifactsDir.mkdirs();
                    if (!success) {
                        listener.getLogger().println("Can't create artifacts directory at " + artifactsDir.getAbsolutePath());
                    }
                }

                final String pathJSONReport = artifactsDir.getCanonicalPath() + DUDE_STATISTICS_JSON_PATH;
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathJSONReport),
                                                                                       StandardCharsets.UTF_8))) {
                    writer.write(new ObjectMapper().writeValueAsString(pluginResults));
                }

                String path = artifactsDir.getCanonicalPath() + DUDE_STATISTICS_HTML_PATH;
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path),
                                                                                       StandardCharsets.UTF_8))) {
                    writer.write(reportHTML);
                }

                return super.tearDown(build, listener);
            }
        };
    }

    private static DuDeStatisticResults buildCurrentDuDeStatisticResults(FilePath root) throws IOException,
                                                                                               InterruptedException {
        DuDeStatisticResults DuDeStatisticResults = null;

        Stack<FilePath> toProcess = new Stack<>();
        toProcess.push(root);
        while (!toProcess.isEmpty()) {
            FilePath path = toProcess.pop();
            if (path.isDirectory()) {
                toProcess.addAll(path.list());
            } else if (path.getName().equals("dude-StatisticResults.json")) {
                final InputStream statisticResultsStream = path.read();
                DuDeStatisticResults = new ObjectMapper().readValue(IOUtils.toString(statisticResultsStream), DuDeStatisticResults.class);
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

        return pluginResults;
    }

    private static String generateHTMLReport(final PluginResults pluginResults) throws IOException {
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
        content = content.replace(NUMBER_OF_FILES_CONTAINING_DUPLICATE_FRAGMENTS, pluginResults.getNumberOfFilesContainingDuplicateFragments());
        content = content.replace(FILES_WITH_DUPLICATE_FRAGMENTS, pluginResults.getFilesWithDuplicateFragments());
        content = content.replace(PERCENTAGE_OF_FILES_ANALYSED_THAT_HAVE_DUPLICATE_FRAGMENTS,
                                  pluginResults.getPercentageOfFilesAnalysedThatHaveDuplicateFragments());

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
