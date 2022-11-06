package org.ljmu.thesis.model;

public class Config {
    private String _readMe;
    private String projectToRun;
    private Path path;
    private Project[] projects;

    public String get_readMe() {
        return _readMe;
    }

    public void set_readMe(String _readMe) {
        this._readMe = _readMe;
    }

    public String getProjectToRun() {
        return projectToRun;
    }

    public void setProjectToRun(String projectToRun) {
        this.projectToRun = projectToRun;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Project[] getProjects() {
        return projects;
    }

    public void setProjects(Project[] projects) {
        this.projects = projects;
    }
}
