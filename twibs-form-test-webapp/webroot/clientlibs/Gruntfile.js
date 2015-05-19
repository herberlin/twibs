module.exports = function (grunt) {
    require('jit-grunt')(grunt);

    grunt.initConfig({
        less: {
            development: {
                options: {
                    compress: true,
                    optimization: 2,
                    sourceMap: true,
                },
                files: {
                    "twibs.css": "css/twibs.less",
                    "twibs-form.css": "css/twibs-form.less"
                }
            }
        },
        uglify: {
            options: {
                compress: {
                    drop_console: true
                }
            },
            development: {
                files: {
                    'twibs.js':
                        [
                            'js/modernizr.custom.35967.js',
                            'js/bootstrap.js',
                            'js/bootstrap-datetimepicker.js',
                            'js/chosen.jquery.js',
                            'js/jquery.appear.js',
                            'js/jquery.bootstrap-touchspin.js',
                            'js/jquery.form.js',
                            'js/pnotify.custom.js',
                            'js/html.sortable.js',
                            'js/twibs.js'
                        ],
                    'twibs-form.js':
                        [
                            'js/twibs-form.js'
                        ],
                    'jquery.js':
                        [
                            'js/jquery-2.1.3.js'
                        ]
                }
            }
        },
        watch: {
            styles: {
                files: ['src/main/webapp/clientlibs/css/**/*.less'],
                tasks: ['less'],
                options: {
                    nospawn: true
                }
            },
            uglify: {
                files: ['src/main/webapp/clientlibs/js/**/*.js'],
                tasks: ['less'],
                options: {
                    nospawn: true
                }
            }
        }
    });

    grunt.registerTask('default', ['less', 'uglify', 'watch']);
};
