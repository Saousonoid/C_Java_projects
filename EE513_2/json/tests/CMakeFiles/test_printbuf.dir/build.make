# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.25

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:

#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:

# Disable VCS-based implicit rules.
% : %,v

# Disable VCS-based implicit rules.
% : RCS/%

# Disable VCS-based implicit rules.
% : RCS/%,v

# Disable VCS-based implicit rules.
% : SCCS/s.%

# Disable VCS-based implicit rules.
% : s.%

.SUFFIXES: .hpux_make_needs_suffix_list

# Command-line flag to silence nested $(MAKE).
$(VERBOSE)MAKESILENT = -s

#Suppress display of executed commands.
$(VERBOSE).SILENT:

# A target that is always out of date.
cmake_force:
.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /usr/bin/cmake

# The command to remove a file.
RM = /usr/bin/cmake -E rm -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/admin/assign2/json-c

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/admin/assign2/json-c-build

# Include any dependencies generated for this target.
include tests/CMakeFiles/test_printbuf.dir/depend.make
# Include any dependencies generated by the compiler for this target.
include tests/CMakeFiles/test_printbuf.dir/compiler_depend.make

# Include the progress variables for this target.
include tests/CMakeFiles/test_printbuf.dir/progress.make

# Include the compile flags for this target's objects.
include tests/CMakeFiles/test_printbuf.dir/flags.make

tests/CMakeFiles/test_printbuf.dir/test_printbuf.c.o: tests/CMakeFiles/test_printbuf.dir/flags.make
tests/CMakeFiles/test_printbuf.dir/test_printbuf.c.o: /home/admin/assign2/json-c/tests/test_printbuf.c
tests/CMakeFiles/test_printbuf.dir/test_printbuf.c.o: tests/CMakeFiles/test_printbuf.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/admin/assign2/json-c-build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building C object tests/CMakeFiles/test_printbuf.dir/test_printbuf.c.o"
	cd /home/admin/assign2/json-c-build/tests && /usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -MD -MT tests/CMakeFiles/test_printbuf.dir/test_printbuf.c.o -MF CMakeFiles/test_printbuf.dir/test_printbuf.c.o.d -o CMakeFiles/test_printbuf.dir/test_printbuf.c.o -c /home/admin/assign2/json-c/tests/test_printbuf.c

tests/CMakeFiles/test_printbuf.dir/test_printbuf.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/test_printbuf.dir/test_printbuf.c.i"
	cd /home/admin/assign2/json-c-build/tests && /usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /home/admin/assign2/json-c/tests/test_printbuf.c > CMakeFiles/test_printbuf.dir/test_printbuf.c.i

tests/CMakeFiles/test_printbuf.dir/test_printbuf.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/test_printbuf.dir/test_printbuf.c.s"
	cd /home/admin/assign2/json-c-build/tests && /usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /home/admin/assign2/json-c/tests/test_printbuf.c -o CMakeFiles/test_printbuf.dir/test_printbuf.c.s

# Object files for target test_printbuf
test_printbuf_OBJECTS = \
"CMakeFiles/test_printbuf.dir/test_printbuf.c.o"

# External object files for target test_printbuf
test_printbuf_EXTERNAL_OBJECTS =

tests/test_printbuf: tests/CMakeFiles/test_printbuf.dir/test_printbuf.c.o
tests/test_printbuf: tests/CMakeFiles/test_printbuf.dir/build.make
tests/test_printbuf: libjson-c.so.5.3.0
tests/test_printbuf: tests/CMakeFiles/test_printbuf.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/home/admin/assign2/json-c-build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Linking C executable test_printbuf"
	cd /home/admin/assign2/json-c-build/tests && $(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/test_printbuf.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
tests/CMakeFiles/test_printbuf.dir/build: tests/test_printbuf
.PHONY : tests/CMakeFiles/test_printbuf.dir/build

tests/CMakeFiles/test_printbuf.dir/clean:
	cd /home/admin/assign2/json-c-build/tests && $(CMAKE_COMMAND) -P CMakeFiles/test_printbuf.dir/cmake_clean.cmake
.PHONY : tests/CMakeFiles/test_printbuf.dir/clean

tests/CMakeFiles/test_printbuf.dir/depend:
	cd /home/admin/assign2/json-c-build && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/admin/assign2/json-c /home/admin/assign2/json-c/tests /home/admin/assign2/json-c-build /home/admin/assign2/json-c-build/tests /home/admin/assign2/json-c-build/tests/CMakeFiles/test_printbuf.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : tests/CMakeFiles/test_printbuf.dir/depend

