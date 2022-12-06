package day7;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Directory {

    private static final Directory root = new Directory(null, "/");
    private final Directory parent;

    private final String name;

    private final Set<Directory> dirs = new HashSet<>();
    private final Set<File> files = new HashSet<>();

    public Directory(Directory parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public boolean isRoot() {
        return root.equals(this);
    }

    public static Directory getRoot() {
        return root;
    }

    public Directory getParent() {
        if (isRoot()) {
            throw new IllegalStateException("Root has no parent");
        }
        else {
            if (parent == null) {
                throw new IllegalStateException("Parent should exist");
            }
            return parent;
        }
    }

    public Directory getSubDirectory(String name) {
        var subdirectory = dirs.stream().filter(dir -> dir.name.equals(name)).findFirst();
        return subdirectory.orElseGet(() -> addSubDirectory(name));
    }

    public Directory addSubDirectory(String name) {
        var subdirectory = new Directory(this, name);
        dirs.add(subdirectory);
        return subdirectory;
    }

    public void addFile(String name, long size) {
        var file = new File(name, size);
        files.add(file);
    }

    public long getSize() {
        return files.stream().map(File::size).reduce(0L, Long::sum);
    }

    public long getTotalSize() {
        return getSize() + dirs.stream().map(Directory::getTotalSize).reduce(0L, Long::sum);
    }

    public Set<Directory> getDirectoriesWithMaximumSize(long maxSize) {
       var subdirs = dirs.stream()
               .map(subdir -> subdir.getDirectoriesWithMaximumSize(maxSize))
               .flatMap(Collection::stream).collect(Collectors.toSet());
        var size = getTotalSize();
        if (size <= maxSize) {
            subdirs.add(this);
        }
        return subdirs;
    }

    public long getTotalSizeWithMaximum(long maxSize) {
        var dirs = getDirectoriesWithMaximumSize(maxSize);
        return dirs.stream().map(Directory::getTotalSize).reduce(0L, Long::sum);
    }

    public Set<Directory> getDirectoriesWithMinimumSize(long minSize) {
        var subdirs = dirs.stream()
                .map(subdir -> subdir.getDirectoriesWithMinimumSize(minSize))
                .flatMap(Collection::stream).collect(Collectors.toSet());
        var size = getTotalSize();
        if (size >= minSize) {
            subdirs.add(this);
        }
        return subdirs;
    }
}
