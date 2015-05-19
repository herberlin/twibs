module.exports = function (grunt) {
    require('jit-grunt')(grunt);

    grunt.initConfig({
        less: {
            development: {
                options: {
                    compress: true,
                    optimization: 2,
                    sourceMap: true,
                    sourceMapBasepath: "src/main/webapp/clientlibs/"
                },
                files: {
                    "target/twibs-form-test-webapp/clientlibs/twibs.css": "src/main/webapp/clientlibs/src/less/twibs/twibs.less"
                }
            }
        },
        watch: {
            styles: {
                files: ['src/main/webapp/clientlibs/src/less/**/*.less'],
                tasks: ['less'],
                options: {
                    nospawn: true
                }
            }
        }
    });

    grunt.registerTask('default', ['less', 'watch']);
};
