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
import io.jenkins.plugins.dude.model.StatisticResults;
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

    private static final String REPORT_TEMPLATE_PATH = "/stats.html";
    private static final String PROJECT_NAME_VAR = "$PROJECT_NAME$";
    private static final String NUMBER_OF_FILES_ANALYSED = "$NUMBER_OF_FILES_ANALYSED$";
    private static final String NUMBER_OF_DUPLICATED_CODE_FRAGMENTS = "$NUMBER_OF_DUPLICATED_CODE_FRAGMENTS$";
    private static final String FILES_WITH_DUPLICATE_FRAGMENTS = "$FILES_WITH_DUPLICATE_FRAGMENTS$";
    private static final String NUMBER_OF_FILES_CONTAINING_DUPLICATE_FRAGMENTS = "$NUMBER_OF_FILES_CONTAINING_DUPLICATE_FRAGMENTS$";
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
                StatisticResults duDeStatisticResults = buildDuDeStatisticResults(build.getWorkspace());
                String report = generateReport(build.getProject().getDisplayName(), duDeStatisticResults);
                File artifactsDir = build.getArtifactsDir();
                if (!artifactsDir.isDirectory()) {
                    boolean success = artifactsDir.mkdirs();
                    if (!success) {
                        listener.getLogger().println("Can't create artifacts directory at "
                                                     + artifactsDir.getAbsolutePath());
                    } else {
                        listener.getLogger().println("Will write report: " + report);
                    }
                }
                String path = artifactsDir.getCanonicalPath() + REPORT_TEMPLATE_PATH;
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path),
                                                                                       StandardCharsets.UTF_8))) {
                    writer.write(report);
                }
                return super.tearDown(build, listener);
            }
        };
    }

    private static StatisticResults buildDuDeStatisticResults(FilePath root) throws IOException, InterruptedException {
        StatisticResults statisticResults = null;

        Stack<FilePath> toProcess = new Stack<>();
        toProcess.push(root);
        while (!toProcess.isEmpty()) {
            FilePath path = toProcess.pop();
            if (path.isDirectory()) {
                toProcess.addAll(path.list());
            } else if (path.getName().equals("dude-StatisticResults.json")) {
                final InputStream statisticResultsStream = path.read();
                statisticResults = new ObjectMapper().readValue(IOUtils.toString(statisticResultsStream), StatisticResults.class);
            }
        }

        return statisticResults;
    }

    private static String generateReport(String projectName, StatisticResults statisticResults) throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        try (InputStream in = StatisticResultsBuildWrapper.class.getResourceAsStream(REPORT_TEMPLATE_PATH)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) >= 0) {
                bOut.write(buffer, 0, read);
            }
        }
        String content = new String(bOut.toByteArray(), StandardCharsets.UTF_8);
        content = content.replace(PROJECT_NAME_VAR, projectName);
        content = content.replace(NUMBER_OF_FILES_ANALYSED, String.valueOf(statisticResults.getNumberOfFilesAnalysed()));
        content = content.replace(NUMBER_OF_DUPLICATED_CODE_FRAGMENTS, String.valueOf(statisticResults.getNumberOfDuplicatedCodeFragments()));
        content = content.replace(NUMBER_OF_FILES_CONTAINING_DUPLICATE_FRAGMENTS,
                                  String.valueOf(statisticResults.getNumberOfFilesContainingDuplicateFragments()));
        content = content.replace(FILES_WITH_DUPLICATE_FRAGMENTS,
                                  getFilesWithDuplicateFragmentsAsString(statisticResults));
        content = content.replace(PERCENTAGE_OF_FILES_ANALYSED_THAT_HAVE_DUPLICATE_FRAGMENTS,
                                  String.valueOf(statisticResults.getPercentageOfFilesAnalysedThatHaveDuplicateFragments()));

        return content;
    }

    private static String getFilesWithDuplicateFragmentsAsString(final StatisticResults statisticResults) {
        return statisticResults.getFilesWithDuplicateFragments()
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
