module.exports = function (grunt) {
    require('jit-grunt')(grunt);

    grunt.initConfig({
        less: {
            development: {
                options: {
                    compress: true,
                    optimization: 2,
                    sourceMap: true,
                    // This is a workaround due to a bug (?) in less or grunt less plugin
                    // https://github.com/gruntjs/grunt-contrib-less/issues/236
                    sourceMapURL: 'twibs.css.map'
                },
                files: [
                    {src: ['src/css/twibs.less'], dest: 'clientlibs/twibs.css'},
                    {src: ['src/css/twibs-form.less'], dest: 'clientlibs/twibs-form.css'}
                ]
            }
        },
        uglify: {
            development: {
                options: {
                    sourceMap: true,
                    compress: {
                        //drop_console: false
                    }
                },
                files: [
                    {
                        src: [
                            'src/js/modernizr.custom.35967.js',
                            'src/js/bootstrap.js',
                            'src/js/bootstrap-datetimepicker.js',
                            'src/js/chosen.jquery.js',
                            'src/js/jquery.appear.js',
                            'src/js/jquery.bootstrap-touchspin.js',
                            'src/js/jquery.form.js',
                            'src/js/pnotify.custom.js',
                            'src/js/html.sortable.js',
                            'src/js/twibs.js'
                        ],
                        dest: 'clientlibs/twibs.js'
                    },
                    {src: ['src/js/twibs-form.js'], dest: 'clientlibs/twibs-form.js'},
                    {src: ['src/js/jquery-2.1.3.js'], dest: 'clientlibs/jquery.js'}
                ]
            }
        },
        copy: {
            development: {
                files: [
                    {expand: true, src: ['clientlibs/**', 'src/**'], dest: '../../../target/twibs-form-test-webapp'}
                ]
            }
        },
        watch: {
            styles: {
                files: ['src/css/**/*.less'],
                tasks: ['less', 'copy'],
                options: {
                    nospawn: true
                }
            },
            uglify: {
                files: ['src/js/**/*.js'],
                tasks: ['uglify', 'copy'],
                options: {
                    nospawn: true
                }
            }
        }
    });

    grunt.registerTask('default', ['less', 'uglify', 'copy', 'watch']);
};
