(function(){function r(e,n,t){function o(i,f){if(!n[i]){if(!e[i]){var c="function"==typeof require&&require;if(!f&&c)return c(i,!0);if(u)return u(i,!0);var a=new Error("Cannot find module '"+i+"'");throw a.code="MODULE_NOT_FOUND",a}var p=n[i]={exports:{}};e[i][0].call(p.exports,function(r){var n=e[i][1][r];return o(n||r)},p,p.exports,r,e,n,t)}return n[i].exports}for(var u="function"==typeof require&&require,i=0;i<t.length;i++)o(t[i]);return o}return r})()({1:[function(require,module,exports){
/*! @preserve
 * numeral.js
 * version : 2.0.6
 * author : Adam Draper
 * license : MIT
 * http://adamwdraper.github.com/Numeral-js/
 */

(function (global, factory) {
    if (typeof define === 'function' && define.amd) {
        define(factory);
    } else if (typeof module === 'object' && module.exports) {
        module.exports = factory();
    } else {
        global.numeral = factory();
    }
}(this, function () {
    /************************************
        Variables
    ************************************/

    var numeral,
        _,
        VERSION = '2.0.6',
        formats = {},
        locales = {},
        defaults = {
            currentLocale: 'en',
            zeroFormat: null,
            nullFormat: null,
            defaultFormat: '0,0',
            scalePercentBy100: true
        },
        options = {
            currentLocale: defaults.currentLocale,
            zeroFormat: defaults.zeroFormat,
            nullFormat: defaults.nullFormat,
            defaultFormat: defaults.defaultFormat,
            scalePercentBy100: defaults.scalePercentBy100
        };


    /************************************
        Constructors
    ************************************/

    // Numeral prototype object
    function Numeral(input, number) {
        this._input = input;

        this._value = number;
    }

    numeral = function(input) {
        var value,
            kind,
            unformatFunction,
            regexp;

        if (numeral.isNumeral(input)) {
            value = input.value();
        } else if (input === 0 || typeof input === 'undefined') {
            value = 0;
        } else if (input === null || _.isNaN(input)) {
            value = null;
        } else if (typeof input === 'string') {
            if (options.zeroFormat && input === options.zeroFormat) {
                value = 0;
            } else if (options.nullFormat && input === options.nullFormat || !input.replace(/[^0-9]+/g, '').length) {
                value = null;
            } else {
                for (kind in formats) {
                    regexp = typeof formats[kind].regexps.unformat === 'function' ? formats[kind].regexps.unformat() : formats[kind].regexps.unformat;

                    if (regexp && input.match(regexp)) {
                        unformatFunction = formats[kind].unformat;

                        break;
                    }
                }

                unformatFunction = unformatFunction || numeral._.stringToNumber;

                value = unformatFunction(input);
            }
        } else {
            value = Number(input)|| null;
        }

        return new Numeral(input, value);
    };

    // version number
    numeral.version = VERSION;

    // compare numeral object
    numeral.isNumeral = function(obj) {
        return obj instanceof Numeral;
    };

    // helper functions
    numeral._ = _ = {
        // formats numbers separators, decimals places, signs, abbreviations
        numberToFormat: function(value, format, roundingFunction) {
            var locale = locales[numeral.options.currentLocale],
                negP = false,
                optDec = false,
                leadingCount = 0,
                abbr = '',
                trillion = 1000000000000,
                billion = 1000000000,
                million = 1000000,
                thousand = 1000,
                decimal = '',
                neg = false,
                abbrForce, // force abbreviation
                abs,
                min,
                max,
                power,
                int,
                precision,
                signed,
                thousands,
                output;

            // make sure we never format a null value
            value = value || 0;

            abs = Math.abs(value);

            // see if we should use parentheses for negative number or if we should prefix with a sign
            // if both are present we default to parentheses
            if (numeral._.includes(format, '(')) {
                negP = true;
                format = format.replace(/[\(|\)]/g, '');
            } else if (numeral._.includes(format, '+') || numeral._.includes(format, '-')) {
                signed = numeral._.includes(format, '+') ? format.indexOf('+') : value < 0 ? format.indexOf('-') : -1;
                format = format.replace(/[\+|\-]/g, '');
            }

            // see if abbreviation is wanted
            if (numeral._.includes(format, 'a')) {
                abbrForce = format.match(/a(k|m|b|t)?/);

                abbrForce = abbrForce ? abbrForce[1] : false;

                // check for space before abbreviation
                if (numeral._.includes(format, ' a')) {
                    abbr = ' ';
                }

                format = format.replace(new RegExp(abbr + 'a[kmbt]?'), '');

                if (abs >= trillion && !abbrForce || abbrForce === 't') {
                    // trillion
                    abbr += locale.abbreviations.trillion;
                    value = value / trillion;
                } else if (abs < trillion && abs >= billion && !abbrForce || abbrForce === 'b') {
                    // billion
                    abbr += locale.abbreviations.billion;
                    value = value / billion;
                } else if (abs < billion && abs >= million && !abbrForce || abbrForce === 'm') {
                    // million
                    abbr += locale.abbreviations.million;
                    value = value / million;
                } else if (abs < million && abs >= thousand && !abbrForce || abbrForce === 'k') {
                    // thousand
                    abbr += locale.abbreviations.thousand;
                    value = value / thousand;
                }
            }

            // check for optional decimals
            if (numeral._.includes(format, '[.]')) {
                optDec = true;
                format = format.replace('[.]', '.');
            }

            // break number and format
            int = value.toString().split('.')[0];
            precision = format.split('.')[1];
            thousands = format.indexOf(',');
            leadingCount = (format.split('.')[0].split(',')[0].match(/0/g) || []).length;

            if (precision) {
                if (numeral._.includes(precision, '[')) {
                    precision = precision.replace(']', '');
                    precision = precision.split('[');
                    decimal = numeral._.toFixed(value, (precision[0].length + precision[1].length), roundingFunction, precision[1].length);
                } else {
                    decimal = numeral._.toFixed(value, precision.length, roundingFunction);
                }

                int = decimal.split('.')[0];

                if (numeral._.includes(decimal, '.')) {
                    decimal = locale.delimiters.decimal + decimal.split('.')[1];
                } else {
                    decimal = '';
                }

                if (optDec && Number(decimal.slice(1)) === 0) {
                    decimal = '';
                }
            } else {
                int = numeral._.toFixed(value, 0, roundingFunction);
            }

            // check abbreviation again after rounding
            if (abbr && !abbrForce && Number(int) >= 1000 && abbr !== locale.abbreviations.trillion) {
                int = String(Number(int) / 1000);

                switch (abbr) {
                    case locale.abbreviations.thousand:
                        abbr = locale.abbreviations.million;
                        break;
                    case locale.abbreviations.million:
                        abbr = locale.abbreviations.billion;
                        break;
                    case locale.abbreviations.billion:
                        abbr = locale.abbreviations.trillion;
                        break;
                }
            }


            // format number
            if (numeral._.includes(int, '-')) {
                int = int.slice(1);
                neg = true;
            }

            if (int.length < leadingCount) {
                for (var i = leadingCount - int.length; i > 0; i--) {
                    int = '0' + int;
                }
            }

            if (thousands > -1) {
                int = int.toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1' + locale.delimiters.thousands);
            }

            if (format.indexOf('.') === 0) {
                int = '';
            }

            output = int + decimal + (abbr ? abbr : '');

            if (negP) {
                output = (negP && neg ? '(' : '') + output + (negP && neg ? ')' : '');
            } else {
                if (signed >= 0) {
                    output = signed === 0 ? (neg ? '-' : '+') + output : output + (neg ? '-' : '+');
                } else if (neg) {
                    output = '-' + output;
                }
            }

            return output;
        },
        // unformats numbers separators, decimals places, signs, abbreviations
        stringToNumber: function(string) {
            var locale = locales[options.currentLocale],
                stringOriginal = string,
                abbreviations = {
                    thousand: 3,
                    million: 6,
                    billion: 9,
                    trillion: 12
                },
                abbreviation,
                value,
                i,
                regexp;

            if (options.zeroFormat && string === options.zeroFormat) {
                value = 0;
            } else if (options.nullFormat && string === options.nullFormat || !string.replace(/[^0-9]+/g, '').length) {
                value = null;
            } else {
                value = 1;

                if (locale.delimiters.decimal !== '.') {
                    string = string.replace(/\./g, '').replace(locale.delimiters.decimal, '.');
                }

                for (abbreviation in abbreviations) {
                    regexp = new RegExp('[^a-zA-Z]' + locale.abbreviations[abbreviation] + '(?:\\)|(\\' + locale.currency.symbol + ')?(?:\\))?)?$');

                    if (stringOriginal.match(regexp)) {
                        value *= Math.pow(10, abbreviations[abbreviation]);
                        break;
                    }
                }

                // check for negative number
                value *= (string.split('-').length + Math.min(string.split('(').length - 1, string.split(')').length - 1)) % 2 ? 1 : -1;

                // remove non numbers
                string = string.replace(/[^0-9\.]+/g, '');

                value *= Number(string);
            }

            return value;
        },
        isNaN: function(value) {
            return typeof value === 'number' && isNaN(value);
        },
        includes: function(string, search) {
            return string.indexOf(search) !== -1;
        },
        insert: function(string, subString, start) {
            return string.slice(0, start) + subString + string.slice(start);
        },
        reduce: function(array, callback /*, initialValue*/) {
            if (this === null) {
                throw new TypeError('Array.prototype.reduce called on null or undefined');
            }

            if (typeof callback !== 'function') {
                throw new TypeError(callback + ' is not a function');
            }

            var t = Object(array),
                len = t.length >>> 0,
                k = 0,
                value;

            if (arguments.length === 3) {
                value = arguments[2];
            } else {
                while (k < len && !(k in t)) {
                    k++;
                }

                if (k >= len) {
                    throw new TypeError('Reduce of empty array with no initial value');
                }

                value = t[k++];
            }
            for (; k < len; k++) {
                if (k in t) {
                    value = callback(value, t[k], k, t);
                }
            }
            return value;
        },
        /**
         * Computes the multiplier necessary to make x >= 1,
         * effectively eliminating miscalculations caused by
         * finite precision.
         */
        multiplier: function (x) {
            var parts = x.toString().split('.');

            return parts.length < 2 ? 1 : Math.pow(10, parts[1].length);
        },
        /**
         * Given a variable number of arguments, returns the maximum
         * multiplier that must be used to normalize an operation involving
         * all of them.
         */
        correctionFactor: function () {
            var args = Array.prototype.slice.call(arguments);

            return args.reduce(function(accum, next) {
                var mn = _.multiplier(next);
                return accum > mn ? accum : mn;
            }, 1);
        },
        /**
         * Implementation of toFixed() that treats floats more like decimals
         *
         * Fixes binary rounding issues (eg. (0.615).toFixed(2) === '0.61') that present
         * problems for accounting- and finance-related software.
         */
        toFixed: function(value, maxDecimals, roundingFunction, optionals) {
            var splitValue = value.toString().split('.'),
                minDecimals = maxDecimals - (optionals || 0),
                boundedPrecision,
                optionalsRegExp,
                power,
                output;

            // Use the smallest precision value possible to avoid errors from floating point representation
            if (splitValue.length === 2) {
              boundedPrecision = Math.min(Math.max(splitValue[1].length, minDecimals), maxDecimals);
            } else {
              boundedPrecision = minDecimals;
            }

            power = Math.pow(10, boundedPrecision);

            // Multiply up by precision, round accurately, then divide and use native toFixed():
            output = (roundingFunction(value + 'e+' + boundedPrecision) / power).toFixed(boundedPrecision);

            if (optionals > maxDecimals - boundedPrecision) {
                optionalsRegExp = new RegExp('\\.?0{1,' + (optionals - (maxDecimals - boundedPrecision)) + '}$');
                output = output.replace(optionalsRegExp, '');
            }

            return output;
        }
    };

    // avaliable options
    numeral.options = options;

    // avaliable formats
    numeral.formats = formats;

    // avaliable formats
    numeral.locales = locales;

    // This function sets the current locale.  If
    // no arguments are passed in, it will simply return the current global
    // locale key.
    numeral.locale = function(key) {
        if (key) {
            options.currentLocale = key.toLowerCase();
        }

        return options.currentLocale;
    };

    // This function provides access to the loaded locale data.  If
    // no arguments are passed in, it will simply return the current
    // global locale object.
    numeral.localeData = function(key) {
        if (!key) {
            return locales[options.currentLocale];
        }

        key = key.toLowerCase();

        if (!locales[key]) {
            throw new Error('Unknown locale : ' + key);
        }

        return locales[key];
    };

    numeral.reset = function() {
        for (var property in defaults) {
            options[property] = defaults[property];
        }
    };

    numeral.zeroFormat = function(format) {
        options.zeroFormat = typeof(format) === 'string' ? format : null;
    };

    numeral.nullFormat = function (format) {
        options.nullFormat = typeof(format) === 'string' ? format : null;
    };

    numeral.defaultFormat = function(format) {
        options.defaultFormat = typeof(format) === 'string' ? format : '0.0';
    };

    numeral.register = function(type, name, format) {
        name = name.toLowerCase();

        if (this[type + 's'][name]) {
            throw new TypeError(name + ' ' + type + ' already registered.');
        }

        this[type + 's'][name] = format;

        return format;
    };


    numeral.validate = function(val, culture) {
        var _decimalSep,
            _thousandSep,
            _currSymbol,
            _valArray,
            _abbrObj,
            _thousandRegEx,
            localeData,
            temp;

        //coerce val to string
        if (typeof val !== 'string') {
            val += '';

            if (console.warn) {
                console.warn('Numeral.js: Value is not string. It has been co-erced to: ', val);
            }
        }

        //trim whitespaces from either sides
        val = val.trim();

        //if val is just digits return true
        if (!!val.match(/^\d+$/)) {
            return true;
        }

        //if val is empty return false
        if (val === '') {
            return false;
        }

        //get the decimal and thousands separator from numeral.localeData
        try {
            //check if the culture is understood by numeral. if not, default it to current locale
            localeData = numeral.localeData(culture);
        } catch (e) {
            localeData = numeral.localeData(numeral.locale());
        }

        //setup the delimiters and currency symbol based on culture/locale
        _currSymbol = localeData.currency.symbol;
        _abbrObj = localeData.abbreviations;
        _decimalSep = localeData.delimiters.decimal;
        if (localeData.delimiters.thousands === '.') {
            _thousandSep = '\\.';
        } else {
            _thousandSep = localeData.delimiters.thousands;
        }

        // validating currency symbol
        temp = val.match(/^[^\d]+/);
        if (temp !== null) {
            val = val.substr(1);
            if (temp[0] !== _currSymbol) {
                return false;
            }
        }

        //validating abbreviation symbol
        temp = val.match(/[^\d]+$/);
        if (temp !== null) {
            val = val.slice(0, -1);
            if (temp[0] !== _abbrObj.thousand && temp[0] !== _abbrObj.million && temp[0] !== _abbrObj.billion && temp[0] !== _abbrObj.trillion) {
                return false;
            }
        }

        _thousandRegEx = new RegExp(_thousandSep + '{2}');

        if (!val.match(/[^\d.,]/g)) {
            _valArray = val.split(_decimalSep);
            if (_valArray.length > 2) {
                return false;
            } else {
                if (_valArray.length < 2) {
                    return ( !! _valArray[0].match(/^\d+.*\d$/) && !_valArray[0].match(_thousandRegEx));
                } else {
                    if (_valArray[0].length === 1) {
                        return ( !! _valArray[0].match(/^\d+$/) && !_valArray[0].match(_thousandRegEx) && !! _valArray[1].match(/^\d+$/));
                    } else {
                        return ( !! _valArray[0].match(/^\d+.*\d$/) && !_valArray[0].match(_thousandRegEx) && !! _valArray[1].match(/^\d+$/));
                    }
                }
            }
        }

        return false;
    };


    /************************************
        Numeral Prototype
    ************************************/

    numeral.fn = Numeral.prototype = {
        clone: function() {
            return numeral(this);
        },
        format: function(inputString, roundingFunction) {
            var value = this._value,
                format = inputString || options.defaultFormat,
                kind,
                output,
                formatFunction;

            // make sure we have a roundingFunction
            roundingFunction = roundingFunction || Math.round;

            // format based on value
            if (value === 0 && options.zeroFormat !== null) {
                output = options.zeroFormat;
            } else if (value === null && options.nullFormat !== null) {
                output = options.nullFormat;
            } else {
                for (kind in formats) {
                    if (format.match(formats[kind].regexps.format)) {
                        formatFunction = formats[kind].format;

                        break;
                    }
                }

                formatFunction = formatFunction || numeral._.numberToFormat;

                output = formatFunction(value, format, roundingFunction);
            }

            return output;
        },
        value: function() {
            return this._value;
        },
        input: function() {
            return this._input;
        },
        set: function(value) {
            this._value = Number(value);

            return this;
        },
        add: function(value) {
            var corrFactor = _.correctionFactor.call(null, this._value, value);

            function cback(accum, curr, currI, O) {
                return accum + Math.round(corrFactor * curr);
            }

            this._value = _.reduce([this._value, value], cback, 0) / corrFactor;

            return this;
        },
        subtract: function(value) {
            var corrFactor = _.correctionFactor.call(null, this._value, value);

            function cback(accum, curr, currI, O) {
                return accum - Math.round(corrFactor * curr);
            }

            this._value = _.reduce([value], cback, Math.round(this._value * corrFactor)) / corrFactor;

            return this;
        },
        multiply: function(value) {
            function cback(accum, curr, currI, O) {
                var corrFactor = _.correctionFactor(accum, curr);
                return Math.round(accum * corrFactor) * Math.round(curr * corrFactor) / Math.round(corrFactor * corrFactor);
            }

            this._value = _.reduce([this._value, value], cback, 1);

            return this;
        },
        divide: function(value) {
            function cback(accum, curr, currI, O) {
                var corrFactor = _.correctionFactor(accum, curr);
                return Math.round(accum * corrFactor) / Math.round(curr * corrFactor);
            }

            this._value = _.reduce([this._value, value], cback);

            return this;
        },
        difference: function(value) {
            return Math.abs(numeral(this._value).subtract(value).value());
        }
    };

    /************************************
        Default Locale && Format
    ************************************/

    numeral.register('locale', 'en', {
        delimiters: {
            thousands: ',',
            decimal: '.'
        },
        abbreviations: {
            thousand: 'k',
            million: 'm',
            billion: 'b',
            trillion: 't'
        },
        ordinal: function(number) {
            var b = number % 10;
            return (~~(number % 100 / 10) === 1) ? 'th' :
                (b === 1) ? 'st' :
                (b === 2) ? 'nd' :
                (b === 3) ? 'rd' : 'th';
        },
        currency: {
            symbol: '$'
        }
    });

    

(function() {
        numeral.register('format', 'bps', {
            regexps: {
                format: /(BPS)/,
                unformat: /(BPS)/
            },
            format: function(value, format, roundingFunction) {
                var space = numeral._.includes(format, ' BPS') ? ' ' : '',
                    output;

                value = value * 10000;

                // check for space before BPS
                format = format.replace(/\s?BPS/, '');

                output = numeral._.numberToFormat(value, format, roundingFunction);

                if (numeral._.includes(output, ')')) {
                    output = output.split('');

                    output.splice(-1, 0, space + 'BPS');

                    output = output.join('');
                } else {
                    output = output + space + 'BPS';
                }

                return output;
            },
            unformat: function(string) {
                return +(numeral._.stringToNumber(string) * 0.0001).toFixed(15);
            }
        });
})();


(function() {
        var decimal = {
            base: 1000,
            suffixes: ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
        },
        binary = {
            base: 1024,
            suffixes: ['B', 'KiB', 'MiB', 'GiB', 'TiB', 'PiB', 'EiB', 'ZiB', 'YiB']
        };

    var allSuffixes =  decimal.suffixes.concat(binary.suffixes.filter(function (item) {
            return decimal.suffixes.indexOf(item) < 0;
        }));
        var unformatRegex = allSuffixes.join('|');
        // Allow support for BPS (http://www.investopedia.com/terms/b/basispoint.asp)
        unformatRegex = '(' + unformatRegex.replace('B', 'B(?!PS)') + ')';

    numeral.register('format', 'bytes', {
        regexps: {
            format: /([0\s]i?b)/,
            unformat: new RegExp(unformatRegex)
        },
        format: function(value, format, roundingFunction) {
            var output,
                bytes = numeral._.includes(format, 'ib') ? binary : decimal,
                suffix = numeral._.includes(format, ' b') || numeral._.includes(format, ' ib') ? ' ' : '',
                power,
                min,
                max;

            // check for space before
            format = format.replace(/\s?i?b/, '');

            for (power = 0; power <= bytes.suffixes.length; power++) {
                min = Math.pow(bytes.base, power);
                max = Math.pow(bytes.base, power + 1);

                if (value === null || value === 0 || value >= min && value < max) {
                    suffix += bytes.suffixes[power];

                    if (min > 0) {
                        value = value / min;
                    }

                    break;
                }
            }

            output = numeral._.numberToFormat(value, format, roundingFunction);

            return output + suffix;
        },
        unformat: function(string) {
            var value = numeral._.stringToNumber(string),
                power,
                bytesMultiplier;

            if (value) {
                for (power = decimal.suffixes.length - 1; power >= 0; power--) {
                    if (numeral._.includes(string, decimal.suffixes[power])) {
                        bytesMultiplier = Math.pow(decimal.base, power);

                        break;
                    }

                    if (numeral._.includes(string, binary.suffixes[power])) {
                        bytesMultiplier = Math.pow(binary.base, power);

                        break;
                    }
                }

                value *= (bytesMultiplier || 1);
            }

            return value;
        }
    });
})();


(function() {
        numeral.register('format', 'currency', {
        regexps: {
            format: /(\$)/
        },
        format: function(value, format, roundingFunction) {
            var locale = numeral.locales[numeral.options.currentLocale],
                symbols = {
                    before: format.match(/^([\+|\-|\(|\s|\$]*)/)[0],
                    after: format.match(/([\+|\-|\)|\s|\$]*)$/)[0]
                },
                output,
                symbol,
                i;

            // strip format of spaces and $
            format = format.replace(/\s?\$\s?/, '');

            // format the number
            output = numeral._.numberToFormat(value, format, roundingFunction);

            // update the before and after based on value
            if (value >= 0) {
                symbols.before = symbols.before.replace(/[\-\(]/, '');
                symbols.after = symbols.after.replace(/[\-\)]/, '');
            } else if (value < 0 && (!numeral._.includes(symbols.before, '-') && !numeral._.includes(symbols.before, '('))) {
                symbols.before = '-' + symbols.before;
            }

            // loop through each before symbol
            for (i = 0; i < symbols.before.length; i++) {
                symbol = symbols.before[i];

                switch (symbol) {
                    case '$':
                        output = numeral._.insert(output, locale.currency.symbol, i);
                        break;
                    case ' ':
                        output = numeral._.insert(output, ' ', i + locale.currency.symbol.length - 1);
                        break;
                }
            }

            // loop through each after symbol
            for (i = symbols.after.length - 1; i >= 0; i--) {
                symbol = symbols.after[i];

                switch (symbol) {
                    case '$':
                        output = i === symbols.after.length - 1 ? output + locale.currency.symbol : numeral._.insert(output, locale.currency.symbol, -(symbols.after.length - (1 + i)));
                        break;
                    case ' ':
                        output = i === symbols.after.length - 1 ? output + ' ' : numeral._.insert(output, ' ', -(symbols.after.length - (1 + i) + locale.currency.symbol.length - 1));
                        break;
                }
            }


            return output;
        }
    });
})();


(function() {
        numeral.register('format', 'exponential', {
        regexps: {
            format: /(e\+|e-)/,
            unformat: /(e\+|e-)/
        },
        format: function(value, format, roundingFunction) {
            var output,
                exponential = typeof value === 'number' && !numeral._.isNaN(value) ? value.toExponential() : '0e+0',
                parts = exponential.split('e');

            format = format.replace(/e[\+|\-]{1}0/, '');

            output = numeral._.numberToFormat(Number(parts[0]), format, roundingFunction);

            return output + 'e' + parts[1];
        },
        unformat: function(string) {
            var parts = numeral._.includes(string, 'e+') ? string.split('e+') : string.split('e-'),
                value = Number(parts[0]),
                power = Number(parts[1]);

            power = numeral._.includes(string, 'e-') ? power *= -1 : power;

            function cback(accum, curr, currI, O) {
                var corrFactor = numeral._.correctionFactor(accum, curr),
                    num = (accum * corrFactor) * (curr * corrFactor) / (corrFactor * corrFactor);
                return num;
            }

            return numeral._.reduce([value, Math.pow(10, power)], cback, 1);
        }
    });
})();


(function() {
        numeral.register('format', 'ordinal', {
        regexps: {
            format: /(o)/
        },
        format: function(value, format, roundingFunction) {
            var locale = numeral.locales[numeral.options.currentLocale],
                output,
                ordinal = numeral._.includes(format, ' o') ? ' ' : '';

            // check for space before
            format = format.replace(/\s?o/, '');

            ordinal += locale.ordinal(value);

            output = numeral._.numberToFormat(value, format, roundingFunction);

            return output + ordinal;
        }
    });
})();


(function() {
        numeral.register('format', 'percentage', {
        regexps: {
            format: /(%)/,
            unformat: /(%)/
        },
        format: function(value, format, roundingFunction) {
            var space = numeral._.includes(format, ' %') ? ' ' : '',
                output;

            if (numeral.options.scalePercentBy100) {
                value = value * 100;
            }

            // check for space before %
            format = format.replace(/\s?\%/, '');

            output = numeral._.numberToFormat(value, format, roundingFunction);

            if (numeral._.includes(output, ')')) {
                output = output.split('');

                output.splice(-1, 0, space + '%');

                output = output.join('');
            } else {
                output = output + space + '%';
            }

            return output;
        },
        unformat: function(string) {
            var number = numeral._.stringToNumber(string);
            if (numeral.options.scalePercentBy100) {
                return number * 0.01;
            }
            return number;
        }
    });
})();


(function() {
        numeral.register('format', 'time', {
        regexps: {
            format: /(:)/,
            unformat: /(:)/
        },
        format: function(value, format, roundingFunction) {
            var hours = Math.floor(value / 60 / 60),
                minutes = Math.floor((value - (hours * 60 * 60)) / 60),
                seconds = Math.round(value - (hours * 60 * 60) - (minutes * 60));

            return hours + ':' + (minutes < 10 ? '0' + minutes : minutes) + ':' + (seconds < 10 ? '0' + seconds : seconds);
        },
        unformat: function(string) {
            var timeArray = string.split(':'),
                seconds = 0;

            // turn hours and minutes into seconds and add them all up
            if (timeArray.length === 3) {
                // hours
                seconds = seconds + (Number(timeArray[0]) * 60 * 60);
                // minutes
                seconds = seconds + (Number(timeArray[1]) * 60);
                // seconds
                seconds = seconds + Number(timeArray[2]);
            } else if (timeArray.length === 2) {
                // minutes
                seconds = seconds + (Number(timeArray[0]) * 60);
                // seconds
                seconds = seconds + Number(timeArray[1]);
            }
            return Number(seconds);
        }
    });
})();

return numeral;
}));

},{}],2:[function(require,module,exports){
//import $ from "jquery";
var FinancialResults = (function () {
    function FinancialResults() {
        this.CM = new CashManagement();
        this.GM = new GlobalManagement();
        this.FM = new FinancialManagement();
        this.IM = new InvestmentManagement();
        this.OM = new OperatingManagement();
    }
    return FinancialResults;
}());
var CashManagement = (function () {
    function CashManagement() {
        this.operatingCashFlow = new Item(new ItemElements);
        this.ChangeOperatingCashFlow = new Item(new ItemElements);
        this.investingCashFlow = new Item(new ItemElements);
        this.ChangeInvestingCashFlow = new Item(new ItemElements);
        this.financingCashFlow = new Item(new ItemElements);
        this.ChangeFinancingCashFlow = new Item(new ItemElements);
        this.CAPEX = new Item(new ItemElements);
        this.changeInCAPEX = new Item(new ItemElements);
        this.adquisitions = new Item(new ItemElements);
        this.changeInAdquisitions = new Item(new ItemElements);
        this.securitiesNet = new Item(new ItemElements);
        this.changeInSecurities = new Item(new ItemElements);
        this.investing = new Item(new ItemElements);
        this.changeInInvesting = new Item(new ItemElements);
        this.FCF = new Item(new ItemElements);
        this.changeInFCF = new Item(new ItemElements);
        this.dividends = new Item(new ItemElements);
        this.changeInDividends = new Item(new ItemElements);
        this.stockRepurchase = new Item(new ItemElements);
        this.changeInStockRepurchase = new Item(new ItemElements);
        this.debtRepayment = new Item(new ItemElements);
        this.changeInDebtRepayment = new Item(new ItemElements);
        this.FCFYield = null;
        this.DividendYield = null;
        this.RepurchasesYield = null;
        this.SecuritiesSaleYield = null;
        this.DebtBuyingYield = null;
        this.CAPEXYield = null;
        this.AdquisitionsYield = null;
        this.InvestingYield = null;
    }
    return CashManagement;
}());
var FinancialManagement = (function () {
    function FinancialManagement() {
        this.netInterestEarningsAfterTaxes = new Item(new ItemElements);
        this.longTermDebt = new Item(new ItemElements);
        this.netDebt = new Item(new ItemElements);
        this.equity = new Item(new ItemElements);
        this.debtToCapitalRatio = new Item(new ItemElements);
        this.netDebtToNetCapitalRatio = new Item(new ItemElements);
        this.spread = new Item(new ItemElements);
        this.netFinancialLeverage = new Item(new ItemElements);
        this.financialLeverage = new Item(new ItemElements);
        this.currentRatio = new Item(new ItemElements);
        this.quickRatio = new Item(new ItemElements);
        this.cashRatio = new Item(new ItemElements);
        this.interestCoverage_operatingIncomeVSinterestexpense = new Item(new ItemElements);
        this.interestCoverage_operatingCashFlowVSinterestexpense = new Item(new ItemElements);
    }
    return FinancialManagement;
}());
var GlobalManagement = (function () {
    function GlobalManagement() {
        this.operatingROA = new Item(new ItemElements);
        this.salesOverAssets = new Item(new ItemElements);
        this.financialLeverageGain = new Item(new ItemElements);
        this.ROE = new Item(new ItemElements);
        this.returnOnTangibleEquity = new Item(new ItemElements);
        this.payOut = new Item(new ItemElements);
        this.dividendYield = new Item(new ItemElements);
        this.FCFOverEquity = new Item(new ItemElements);
        this.FCFPerShare = new Item(new ItemElements);
        this.earningsPerShare = new Item(new ItemElements);
        this.operatingIncomePerShare = new Item(new ItemElements);
        this.growthRate = new Item(new ItemElements);
        this.salesGrowthRate = new Item(new ItemElements);
        this.NOPATMargin = new Item(new ItemElements);
        this.beginningNetOperatingWCOverSales = new Item(new ItemElements);
        this.beginningNetOperatingLTAssetsOverSales = new Item(new ItemElements);
        this.beginningNetDebt2CapitalRatio = new Item(new ItemElements);
        this.afterTaxCostOfDebt = new Item(new ItemElements);
    }
    return GlobalManagement;
}());
var InvestmentManagement = (function () {
    function InvestmentManagement() {
        this.LTAssets = new Item(new ItemElements);
        this.accountsPayable = new Item(new ItemElements);
        this.accountsPayableSales = new Item(new ItemElements);
        this.accountsReceivable = new Item(new ItemElements);
        this.accountsReceivableGrowth = new Item(new ItemElements);
        this.accountsReceivableOverSales = new Item(new ItemElements);
        this.cashAndMarketableSecurities = new Item(new ItemElements);
        this.cashOverNetAssets = new Item(new ItemElements);
        this.cashOverReceivables = new Item(new ItemElements);
        this.currentAssets = new Item(new ItemElements);
        this.currentLiabilities = new Item(new ItemElements);
        this.daysInventory = new Item(new ItemElements);
        this.daysPayables = new Item(new ItemElements);
        this.daysReceivables = new Item(new ItemElements);
        this.goodwillAndIntangibles = new Item(new ItemElements);
        this.inventory = new Item(new ItemElements);
        this.inventoryGrowth = new Item(new ItemElements);
        this.inventorySales = new Item(new ItemElements);
        this.longTermDebt = new Item(new ItemElements);
        this.netAssets = new Item(new ItemElements);
        this.netLTAssets = new Item(new ItemElements);
        this.netWorkingCapital = new Item(new ItemElements);
        this.nonInterestBearingLTLiabilities = new Item(new ItemElements);
        this.salesOverNetAssets = new Item(new ItemElements);
        this.salesOverNetLTAssets = new Item(new ItemElements);
        this.salesOverWorkingCapital = new Item(new ItemElements);
        this.shortTermDebtAndCurrentPortionOfLongTermDebt = new Item(new ItemElements);
    }
    return InvestmentManagement;
}());
var OperatingManagement = (function () {
    function OperatingManagement() {
        this.revenue = new Item(new ItemElements);
        this.salesGrowth = new Item(new ItemElements);
        this.COGS = new Item(new ItemElements);
        this.operatingIncome = new Item(new ItemElements);
        this.grossMargin = new Item(new ItemElements);
        this.SGA = new Item(new ItemElements);
        this.SGAOverSales = new Item(new ItemElements);
        this.NOPAT = new Item(new ItemElements);
        this.NOPATGrowth = new Item(new ItemElements);
        this.NOPATMargin = new Item(new ItemElements);
        this.operatingMargin = new Item(new ItemElements);
        this.provisionForIncomeTaxes = new Item(new ItemElements);
        this.taxRate = new Item(new ItemElements);
        this.interestExpense = new Item(new ItemElements);
        this.netIncome = new Item(new ItemElements);
        this.incomeOverRevenue = new Item(new ItemElements);
        this.operatingCashFlow = new Item(new ItemElements);
        this.operatingCashFlowOverIncome = new Item(new ItemElements);
    }
    return OperatingManagement;
}());
var Item = (function () {
    function Item(item) {
        this.item = item;
    }
    Item.prototype.length = function () {
        return 5;
    };
    return Item;
}());
var ItemElements = (function () {
    function ItemElements() {
        this.item1 = 0;
        this.item2 = 0;
        this.item3 = 0;
        this.item4 = 0;
        this.item5 = 0;
    }
    return ItemElements;
}());
function lastYear(json) {
    var ly = "";
    for (var keyFR in json) {
        if (json.hasOwnProperty(keyFR)) {
            ly = keyFR;
        }
    }
    return ly;
}
//import * as numeral from '../../node_modules/numeral/numeral';
$(document).ready(function () {
    $("#analizar").click(function () {
        var company = document.getElementById("company").value;
        console.log(company);
        $.get("https://api.adarga.org/analysis?com=" + company, function (data) {
            /***********************
            
                    Construct the HTML code needed
        
                */
            /**
        
                MOBILE
        
            */
            var VM = document.getElementById("VM");
            VM.style.visibility = "visible";
            var CMM = document.getElementById("CMM");
            CMM.style.visibility = "visible";
            var GMM = document.getElementById("GMM");
            GMM.style.visibility = "visible";
            var OMM = document.getElementById("OMM");
            OMM.style.visibility = "visible";
            var IMM = document.getElementById("IMM");
            IMM.style.visibility = "visible";
            var FMM = document.getElementById("FMM");
            FMM.style.visibility = "visible";
            /**
        
                DESKTOP
        
            */
            var V = document.getElementById("V");
            V.style.visibility = "visible";
            var bodyV = document.getElementById("tbodyV");
            var tableV = document.getElementById("tableV");
            tableV.removeChild(bodyV);
            var CM = document.getElementById("CM");
            CM.style.visibility = "visible";
            var bodyCM = document.getElementById("tbodyCM");
            var tableCM = document.getElementById("tableCM");
            tableCM.removeChild(bodyCM);
            var GM = document.getElementById("GM");
            GM.style.visibility = "visible";
            var bodyGM = document.getElementById("tbodyGM");
            var tableGM = document.getElementById("tableGM");
            tableGM.removeChild(bodyGM);
            var OM = document.getElementById("OM");
            OM.style.visibility = "visible";
            var bodyOM = document.getElementById("tbodyOM");
            var tableOM = document.getElementById("tableOM");
            tableOM.removeChild(bodyOM);
            var IM = document.getElementById("IM");
            IM.style.visibility = "visible";
            var bodyIM = document.getElementById("tbodyIM");
            var tableIM = document.getElementById("tableIM");
            tableIM.removeChild(bodyIM);
            var FM = document.getElementById("FM");
            FM.style.visibility = "visible";
            var bodyFM = document.getElementById("tbodyFM");
            var tableFM = document.getElementById("tableFM");
            tableFM.removeChild(bodyFM);
            var fr = JSON.parse(data);
            console.log(fr);
            //require('typescript-require');
            var numeral = require('../../node_modules/numeral/numeral');
            /***********************
       
               Valuation
   
           */
            var itemNameV = ["FCF Yield",
                "Dividend Yield",
                "Repurchases Yield",
                "Securities Sale Yield",
                "Debt Issuance Yield",
                "CAPEX Yield",
                "Adquisitions Yield",
                "Investing Yield",
            ];
            var itemNumbersV = [fr.CM.FCFYield,
                fr.CM.DividendYield,
                fr.CM.RepurchasesYield,
                fr.CM.SecuritiesSaleYield,
                fr.CM.DebtBuyingYield,
                fr.CM.CAPEXYield,
                fr.CM.AdquisitionsYield,
                fr.CM.InvestingYield,
            ];
            var bodyV = document.createElement("tbody");
            bodyV.setAttribute("id", "tbodyV");
            var dummy7 = document.getElementById("tableV");
            dummy7.appendChild(bodyV);
            var col_1V = document.getElementById("col_1V");
            col_1V.innerHTML = "Yield";
            for (var j = 0; j < itemNumbersV.length; j++) {
                console.log(itemNameV[j]);
                console.log(itemNumbersV[j]);
                var th = document.createElement("th").appendChild(document.createTextNode(itemNameV[j]));
                var td = document.createElement("td");
                var tr = document.createElement("tr");
                td.appendChild(th);
                tr.appendChild(td);
                var convertido = itemNumbersV[j].toLocaleString(undefined, { style: 'percent', minimumFractionDigits: 1 });
                var thi = document.createElement("th").appendChild(document.createTextNode(convertido));
                var tdi = document.createElement("td");
                tdi.id = itemNameV[j];
                tdi.appendChild(thi);
                tr.appendChild(tdi);
                var dummy8 = document.getElementById("tbodyV");
                dummy8.appendChild(tr);
            }
            /**
   
               MOBILE Valuation
   
           */
            for (var j = 0; j < itemNumbersV.length; j++) {
                // Creating table and the header
                var id = document.createElement("id");
                id.setAttribute("class", "table-responsive table-striped w-75 mx-auto");
                var table = document.createElement("table");
                table.setAttribute("class", "table table-hover table-striped w-75 mx-auto");
                var thead = document.createElement("thead");
                thead.setAttribute("class", "thead-dark");
                var tr = document.createElement("tr");
                tr.setAttribute("scope", "col");
                var itenName = document.createElement("th").appendChild(document.createTextNode(""));
                var tHeader = document.createElement("th");
                tHeader.setAttribute("scope", "col");
                tHeader.appendChild(itenName);
                var noChar = document.createElement("th").appendChild(document.createTextNode("          "));
                var tHeader2 = document.createElement("th");
                tHeader2.setAttribute("scope", "col");
                tHeader2.appendChild(noChar);
                tr.appendChild(tHeader);
                tr.appendChild(tHeader2);
                thead.appendChild(tr);
                table.appendChild(thead);
                id.appendChild(table);
                // Creating rows of the table per year
                var tbody = document.createElement("tbody");
                for (var j = 0; j < itemNumbersV.length; j++) {
                    var th = document.createElement("th").appendChild(document.createTextNode(itemNameV[j]));
                    var td = document.createElement("td");
                    var tr = document.createElement("tr");
                    td.appendChild(th);
                    tr.appendChild(td);
                    var convertido = itemNumbersV[j].toLocaleString(undefined, { style: 'percent', minimumFractionDigits: 1 });
                    var thi = document.createElement("th").appendChild(document.createTextNode(convertido));
                    var tdi = document.createElement("td");
                    tdi.id = itemNameV[j];
                    tdi.appendChild(thi);
                    tr.appendChild(tdi);
                    tbody.appendChild(tr);
                }
                table.appendChild(tbody);
                var dummy10 = document.getElementById("VM");
                dummy10.appendChild(table);
            }
            /***********************
       
               Cash Management
   
           */
            var itemNameCM = [
                "FCF",
                "Change In FCF",
                "Operating Cash Flow",
                "Change Operating Cash Flow",
                "Investing Cash Flow",
                "Change Investing Cash Flow",
                "Financing Cash Flow",
                "Change Financing Cash Flow",
                "CAPEX",
                "Change In CAPEX",
                "Adquisitions",
                "Change In Adquisitions",
                "CAPEX + Adquisitions",
                "Change In CAPEX + Adquisitions",
                "Securities Sale Net",
                "Change In Securities",
                "Dividends",
                "Change In Dividends",
                "Stock Repurchase Net",
                "Change In Stock Repurchase",
                "Debt Issuance Net",
                "Change In Debt Repayment",
            ];
            var itemNumbersCM = [
                fr.CM.FCF,
                fr.CM.changeInFCF,
                fr.CM.operatingCashFlow,
                fr.CM.ChangeOperatingCashFlow,
                fr.CM.investingCashFlow,
                fr.CM.ChangeInvestingCashFlow,
                fr.CM.financingCashFlow,
                fr.CM.ChangeFinancingCashFlow,
                fr.CM.CAPEX,
                fr.CM.changeInCAPEX,
                fr.CM.adquisitions,
                fr.CM.changeInAdquisitions,
                fr.CM.investing,
                fr.CM.changeInInvesting,
                fr.CM.securitiesNet,
                fr.CM.changeInSecurities,
                fr.CM.dividends,
                fr.CM.changeInDividends,
                fr.CM.stockRepurchase,
                fr.CM.changeInStockRepurchase,
                fr.CM.debtRepayment,
                fr.CM.changeInDebtRepayment,
            ];
            var bodyCMM = document.createElement("tbody");
            bodyCM.setAttribute("id", "tbodyCM");
            var dummy7 = document.getElementById("tableCM");
            dummy7.appendChild(bodyCM);
            var lastyear = lastYear(fr.CM.operatingCashFlow);
            var col_1CM = document.getElementById("col_1CM");
            col_1CM.innerHTML = String(lastyear);
            var col_2CM = document.getElementById("col_2CM");
            col_2CM.innerHTML = String(parseInt(lastyear) - 1);
            var col_3CM = document.getElementById("col_3CM");
            col_3CM.innerHTML = String(parseInt(lastyear) - 2);
            var col_4CM = document.getElementById("col_4CM");
            col_4CM.innerHTML = String(parseInt(lastyear) - 3);
            var col_5CM = document.getElementById("col_5CM");
            col_5CM.innerHTML = String(parseInt(lastyear) - 4);
            for (var j = 0; j < itemNumbersCM.length; j++) {
                var th = document.createElement("th").appendChild(document.createTextNode(itemNameCM[j]));
                var td = document.createElement("td");
                var tr = document.createElement("tr");
                td.appendChild(th);
                tr.appendChild(td);
                for (var i = 0; i < 5; i++) {
                    var convertido;
                    var itemPaint4 = itemNumbersCM[j];
                    itemPaint = itemPaint4[parseInt(lastyear) - i];
                    if ((typeof itemPaint) == 'number') {
                        convertido = itemPaint.toLocaleString(undefined, { style: 'percent', minimumFractionDigits: 1 });
                        if (itemNameCM[j] == "CAPEX") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                        if (itemNameCM[j] == "Securities Sale Net") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                        if (itemNameCM[j] == "Adquisitions") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                        if (itemNameCM[j] == "CAPEX + Adquisitions") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                        if (itemNameCM[j] == "Investing (CAPEX + Adquisitions)") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                        if (itemNameCM[j] == "Dividends") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                        if (itemNameCM[j] == "Stock Repurchase Net") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                        if (itemNameCM[j] == "Debt Issuance Net") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                    }
                    else {
                        convertido = numeral(Number(itemPaint)).format('0,0');
                    }
                    if (itemPaint > 1000000) {
                        convertido = "Inf";
                    }
                    var thi = document.createElement("th").appendChild(document.createTextNode(convertido));
                    var tdi = document.createElement("td");
                    tdi.id = itemNameCM[j] + i;
                    tdi.appendChild(thi);
                    tr.appendChild(tdi);
                }
                var dummy8 = document.getElementById("tbodyCM");
                dummy8.appendChild(tr);
            }
            /**
   
               MOBILE Cash Management
   
           */
            for (var j = 0; j < itemNumbersCM.length; j++) {
                // Creating table and the header
                var id = document.createElement("id");
                id.setAttribute("class", "table-responsive table-striped w-75 mx-auto");
                var table = document.createElement("table");
                table.setAttribute("class", "table table-hover table-striped w-75 mx-auto");
                var thead = document.createElement("thead");
                thead.setAttribute("class", "thead-dark");
                var tr = document.createElement("tr");
                tr.setAttribute("scope", "col");
                var itenName = document.createElement("th").appendChild(document.createTextNode(itemNameCM[j]));
                var tHeader = document.createElement("th");
                tHeader.setAttribute("scope", "col");
                tHeader.appendChild(itenName);
                var noChar = document.createElement("th").appendChild(document.createTextNode("          "));
                var tHeader2 = document.createElement("th");
                tHeader2.setAttribute("scope", "col");
                tHeader2.appendChild(noChar);
                tr.appendChild(tHeader);
                tr.appendChild(tHeader2);
                thead.appendChild(tr);
                table.appendChild(thead);
                id.appendChild(table);
                // Creating rows of the table per year
                var tbody = document.createElement("tbody");
                for (var i = 0; i < 5; i++) {
                    var convertido;
                    var itemPaint2 = itemNumbersCM[j];
                    var itemPaint = itemPaint2[parseInt(lastyear) - i];
                    if ((typeof itemPaint) == 'number') {
                        convertido = itemPaint.toLocaleString(undefined, { style: 'percent', minimumFractionDigits: 1 });
                        if (itemNameCM[j] == "CAPEX") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                        if (itemNameCM[j] == "Adquisitions") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                        if (itemNameCM[j] == "CAPEX + Adquisitions") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                        if (itemNameCM[j] == "Securities Sale Net") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                        if (itemNameCM[j] == "Investing (CAPEX + Adquisitions)") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                        if (itemNameCM[j] == "Dividends") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                        if (itemNameCM[j] == "Stock Repurchase Net") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                        if (itemNameCM[j] == "Debt Issuance Net") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                    }
                    else {
                        convertido = numeral(Number(itemPaint)).format('0,0');
                    }
                    if (itemPaint > 1000000) {
                        convertido = "Inf";
                    }
                    var yeari = parseInt(lastyear) - i;
                    var yeariS = yeari.toString();
                    var year = document.createElement("td").appendChild(document.createTextNode(yeariS));
                    var tdYear = document.createElement("td");
                    tdYear.appendChild(year);
                    var value = document.createElement("td").appendChild(document.createTextNode(convertido));
                    var tdValue = document.createElement("td");
                    tdValue.appendChild(value);
                    var tr = document.createElement("tr");
                    tr.appendChild(tdYear);
                    tr.appendChild(tdValue);
                    tbody.appendChild(tr);
                }
                table.appendChild(tbody);
                var dummy10 = document.getElementById("CMM");
                dummy10.appendChild(table);
            }
            /***********************
        
                Global Management
    
            */
            var itemNameGM = ["Operating ROA",
                "Sales Over Assets",
                "Financial Leverage Gain",
                "ROE",
                "Return On Tangible Equity",
                "Pay Out",
                "Dividend Yield",
                "FCF Over Equity",
                "FCF Per Share",
                "Earnings Per Share",
                "Operating Income Per Share",
                "Growth Rate",
                "Sales Growth Rate",
                "NOPAT Margin",
                "Beginning Net Operating WC Over Sales",
                "Beginning Net Operating LT Assets Over Sales",
                "Beginning Net Debt 2 Capital Ratio",
                "After TaxCost Of Debt",
            ];
            var itemNumbersGM = [fr.GM.operatingROA,
                fr.GM.salesOverAssets,
                fr.GM.financialLeverageGain,
                fr.GM.ROE,
                fr.GM.returnOnTangibleEquity,
                fr.GM.payOut,
                fr.GM.dividendYield,
                fr.GM.FCFOverEquity,
                fr.GM.FCFPerShare,
                fr.GM.earningsPerShare,
                fr.GM.operatingIncomePerShare,
                fr.GM.growthRate,
                fr.GM.salesGrowthRate,
                fr.GM.NOPATMargin,
                fr.GM.beginningNetOperatingWCOverSales,
                fr.GM.beginningNetOperatingLTAssetsOverSales,
                fr.GM.beginningNetDebt2CapitalRatio,
                fr.GM.afterTaxCostOfDebt,
            ];
            var bodyGM = document.createElement("tbody");
            bodyGM.setAttribute("id", "tbodyGM");
            var dummy9 = document.getElementById("tableGM");
            dummy9.appendChild(bodyGM);
            var lastyear = lastYear(fr.CM.operatingCashFlow);
            var col_1GM = document.getElementById("col_1GM");
            col_1GM.innerHTML = String(lastyear);
            var col_2GM = document.getElementById("col_2GM");
            col_2GM.innerHTML = String(parseInt(lastyear) - 1);
            var col_3GM = document.getElementById("col_3GM");
            col_3GM.innerHTML = String(parseInt(lastyear) - 2);
            var col_4GM = document.getElementById("col_4GM");
            col_4GM.innerHTML = String(parseInt(lastyear) - 3);
            var col_5GM = document.getElementById("col_5GM");
            col_5GM.innerHTML = String(parseInt(lastyear) - 4);
            for (var j = 0; j < itemNumbersGM.length; j++) {
                var th = document.createElement("th").appendChild(document.createTextNode(itemNameGM[j]));
                var td = document.createElement("td");
                var tr = document.createElement("tr");
                td.appendChild(th);
                tr.appendChild(td);
                for (var i = 0; i < 5; i++) {
                    var convertido;
                    var itemPaint5 = itemNumbersGM[j];
                    itemPaint = itemPaint5[parseInt(lastyear) - i];
                    if ((typeof itemPaint) == 'number') {
                        convertido = itemPaint.toLocaleString(undefined, { style: 'percent', minimumFractionDigits: 1 });
                        if (itemNameGM[j] == "FCF Per Share") {
                            convertido = numeral(itemPaint).format('0,0.00');
                        }
                        if (itemNameGM[j] == "Earnings Per Share") {
                            convertido = numeral(itemPaint).format('0,0.00');
                        }
                        if (itemNameGM[j] == "Operating Income Per Share") {
                            convertido = numeral(itemPaint).format('0,0.00');
                        }
                    }
                    else {
                        convertido = numeral(Number(itemPaint)).format('0,0');
                    }
                    if (itemPaint > 1000000) {
                        convertido = "Inf";
                    }
                    var thi = document.createElement("th").appendChild(document.createTextNode(convertido));
                    var tdi = document.createElement("td");
                    tdi.id = itemNameGM[j] + i;
                    tdi.appendChild(thi);
                    tr.appendChild(tdi);
                }
                var dummy10 = document.getElementById("tbodyGM");
                dummy10.appendChild(tr);
            }
            /**
   
               MOBILE Global Management
   
           */
            for (var j = 0; j < itemNumbersGM.length; j++) {
                // Creating table and the header
                var id = document.createElement("id");
                id.setAttribute("class", "table-responsive table-striped w-75 mx-auto");
                var table = document.createElement("table");
                table.setAttribute("class", "table table-hover table-striped w-75 mx-auto");
                var thead = document.createElement("thead");
                thead.setAttribute("class", "thead-dark");
                var tr = document.createElement("tr");
                tr.setAttribute("scope", "col");
                var itenName = document.createElement("th").appendChild(document.createTextNode(itemNameGM[j]));
                var tHeader = document.createElement("th");
                tHeader.setAttribute("scope", "col");
                tHeader.appendChild(itenName);
                var noChar = document.createElement("th").appendChild(document.createTextNode("          "));
                var tHeader2 = document.createElement("th");
                tHeader2.setAttribute("scope", "col");
                tHeader2.appendChild(noChar);
                tr.appendChild(tHeader);
                tr.appendChild(tHeader2);
                thead.appendChild(tr);
                table.appendChild(thead);
                id.appendChild(table);
                // Creating rows of the table per year
                var tbody = document.createElement("tbody");
                for (var i = 0; i < 5; i++) {
                    var convertido;
                    var itemPaint2 = itemNumbersGM[j];
                    var itemPaint = itemPaint2[parseInt(lastyear) - i];
                    if ((typeof itemPaint) == 'number') {
                        convertido = itemPaint.toLocaleString(undefined, { style: 'percent', minimumFractionDigits: 1 });
                        if (itemNameGM[j] == "FCF Per Share") {
                            convertido = numeral(itemPaint).format('0,0.00');
                        }
                        if (itemNameGM[j] == "Earnings Per Share") {
                            convertido = numeral(itemPaint).format('0,0.00');
                        }
                        if (itemNameGM[j] == "Operating Income Per Share") {
                            convertido = numeral(itemPaint).format('0,0.00');
                        }
                    }
                    else {
                        convertido = numeral(Number(itemPaint)).format('0,0');
                    }
                    if (itemPaint > 1000000) {
                        convertido = "Inf";
                    }
                    var yeari = parseInt(lastyear) - i;
                    var yeariS = yeari.toString();
                    var year = document.createElement("td").appendChild(document.createTextNode(yeariS));
                    var tdYear = document.createElement("td");
                    tdYear.appendChild(year);
                    var value = document.createElement("td").appendChild(document.createTextNode(convertido));
                    var tdValue = document.createElement("td");
                    tdValue.appendChild(value);
                    var tr = document.createElement("tr");
                    tr.appendChild(tdYear);
                    tr.appendChild(tdValue);
                    tbody.appendChild(tr);
                }
                table.appendChild(tbody);
                var dummy10 = document.getElementById("GMM");
                dummy10.appendChild(table);
            }
            /***********************
        
                Operating Management
    
            */
            var itemNameOM = ["Revenue",
                "Sales Growth",
                "COGs",
                "Operating Income",
                "Gross Margin",
                "SGA",
                "SGA Growth",
                "NOPAT",
                "NOPAT Growth",
                "NOPAT Margin",
                "Tax Rate",
                "Net Income",
                "Income Over Revenue",
                "Operating Cash Flow Over Income"
            ];
            var itemNumbersOM = [fr.OM.revenue,
                fr.OM.salesGrowth,
                fr.OM.COGS,
                fr.OM.operatingIncome,
                fr.OM.grossMargin,
                fr.OM.SGA,
                fr.OM.SGAOverSales,
                fr.OM.NOPAT,
                fr.OM.NOPATGrowth,
                fr.OM.NOPATMargin,
                fr.OM.taxRate,
                fr.OM.netIncome,
                fr.OM.incomeOverRevenue,
                fr.OM.operatingCashFlowOverIncome
            ];
            var body = document.createElement("tbody");
            body.setAttribute("id", "tbodyOM");
            var dummy = document.getElementById("tableOM");
            dummy.appendChild(body);
            var lastyear = lastYear(fr.OM.revenue);
            var col_1OM = document.getElementById("col_1OM");
            col_1OM.innerHTML = String(lastyear);
            var col_2OM = document.getElementById("col_2OM");
            col_2OM.innerHTML = String(parseInt(lastyear) - 1);
            var col_3OM = document.getElementById("col_3OM");
            col_3OM.innerHTML = String(parseInt(lastyear) - 2);
            var col_4OM = document.getElementById("col_4OM");
            col_4OM.innerHTML = String(parseInt(lastyear) - 3);
            var col_5OM = document.getElementById("col_5OM");
            col_5OM.innerHTML = String(parseInt(lastyear) - 4);
            //  itemNumbersOM
            for (var j = 0; j < itemNumbersOM.length; j++) {
                var th = document.createElement("th").appendChild(document.createTextNode(itemNameOM[j]));
                var td = document.createElement("td");
                var tr = document.createElement("tr");
                td.appendChild(th);
                tr.appendChild(td);
                for (var i = 0; i < 5; i++) {
                    var convertido;
                    var itemPaint2 = itemNumbersOM[j];
                    console.log("itemPaint2");
                    console.log(itemPaint2);
                    var itemPaint = itemPaint2[parseInt(lastyear) - i];
                    if ((typeof itemPaint) == 'number') {
                        convertido = itemPaint.toLocaleString(undefined, { style: 'percent', minimumFractionDigits: 1 });
                        if (itemNameOM[j] == "Operating Income") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                        if (itemNameOM[j] == "NOPAT") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                    }
                    else {
                        convertido = numeral(Number(itemPaint)).format('0,0');
                    }
                    if (itemPaint > 1000000) {
                        convertido = "Inf";
                    }
                    var thi = document.createElement("th").appendChild(document.createTextNode(convertido));
                    var tdi = document.createElement("td");
                    tdi.id = itemNameOM[j] + i;
                    tdi.appendChild(thi);
                    tr.appendChild(tdi);
                }
                var dummy3 = document.getElementById("tbodyOM");
                dummy3.appendChild(tr);
            }
            /**
    
                MOBILE Operating Management
    
            */
            for (var j = 0; j < itemNumbersOM.length; j++) {
                // Creating table and the header
                var id = document.createElement("id");
                id.setAttribute("class", "table-responsive table-striped w-75 mx-auto");
                var table = document.createElement("table");
                table.setAttribute("class", "table table-hover table-striped w-75 mx-auto");
                var thead = document.createElement("thead");
                thead.setAttribute("class", "thead-dark");
                var tr = document.createElement("tr");
                tr.setAttribute("scope", "col");
                var itenName = document.createElement("th").appendChild(document.createTextNode(itemNameOM[j]));
                var tHeader = document.createElement("th");
                tHeader.setAttribute("scope", "col");
                tHeader.appendChild(itenName);
                var noChar = document.createElement("th").appendChild(document.createTextNode("          "));
                var tHeader2 = document.createElement("th");
                tHeader2.setAttribute("scope", "col");
                tHeader2.appendChild(noChar);
                tr.appendChild(tHeader);
                tr.appendChild(tHeader2);
                thead.appendChild(tr);
                table.appendChild(thead);
                id.appendChild(table);
                // Creating rows of the table per year
                var tbody = document.createElement("tbody");
                for (var i = 0; i < 5; i++) {
                    var convertido;
                    var itemPaint2 = itemNumbersOM[j];
                    var itemPaint = itemPaint2[parseInt(lastyear) - i];
                    if ((typeof itemPaint) == 'number') {
                        convertido = itemPaint.toLocaleString(undefined, { style: 'percent', minimumFractionDigits: 1 });
                        if (itemNameOM[j] == "Operating Income") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                        if (itemNameOM[j] == "NOPAT") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                    }
                    else {
                        convertido = numeral(Number(itemPaint)).format('0,0');
                    }
                    if (itemPaint > 1000000) {
                        convertido = "Inf";
                    }
                    var yeari = parseInt(lastyear) - i;
                    var yeariS = yeari.toString();
                    var year = document.createElement("td").appendChild(document.createTextNode(yeariS));
                    var tdYear = document.createElement("td");
                    tdYear.appendChild(year);
                    var value = document.createElement("td").appendChild(document.createTextNode(convertido));
                    var tdValue = document.createElement("td");
                    tdValue.appendChild(value);
                    var tr = document.createElement("tr");
                    tr.appendChild(tdYear);
                    tr.appendChild(tdValue);
                    tbody.appendChild(tr);
                }
                table.appendChild(tbody);
                var dummy10 = document.getElementById("OMM");
                dummy10.appendChild(table);
            }
            /***********************
        
                Investment Management
    
            */
            var itemNameIM = ["Accounts Receivable Growth",
                "Inventory Growth",
                "Accounts Receivable Over Sales",
                "Inventory Over Sales",
                "Days Receivables",
                "Days Inventory",
                "Days Payables",
                "Sales Over Working Capital",
                "Sales Over Net LTAssets",
                "Sales Over Net Assets",
                "Cash Over NetAssets",
                "Cash Over Receivables",
            ];
            var itemNumbersIM = [fr.IM.accountsReceivableGrowth,
                fr.IM.inventoryGrowth,
                fr.IM.accountsReceivableOverSales,
                fr.IM.inventorySales,
                fr.IM.daysReceivables,
                fr.IM.daysInventory,
                fr.IM.daysPayables,
                fr.IM.salesOverWorkingCapital,
                fr.IM.salesOverNetLTAssets,
                fr.IM.salesOverNetAssets,
                fr.IM.cashOverNetAssets,
                fr.IM.cashOverReceivables,
            ];
            var bodyIM = document.createElement("tbody");
            bodyIM.setAttribute("id", "tbodyIM");
            var dummy2 = document.getElementById("tableIM");
            dummy2.appendChild(bodyIM);
            var lastyear = lastYear(fr.IM.cashAndMarketableSecurities);
            var col_1IM = document.getElementById("col_1IM");
            col_1IM.innerHTML = String(lastyear);
            var col_2IM = document.getElementById("col_2IM");
            col_2IM.innerHTML = String(parseInt(lastyear) - 1);
            var col_3IM = document.getElementById("col_3IM");
            col_3IM.innerHTML = String(parseInt(lastyear) - 2);
            var col_4IM = document.getElementById("col_4IM");
            col_4IM.innerHTML = String(parseInt(lastyear) - 3);
            var col_5IM = document.getElementById("col_5IM");
            col_5IM.innerHTML = String(parseInt(lastyear) - 4);
            for (var j = 0; j < itemNumbersIM.length; j++) {
                var th = document.createElement("th").appendChild(document.createTextNode(itemNameIM[j]));
                var td = document.createElement("td");
                var tr = document.createElement("tr");
                td.appendChild(th);
                tr.appendChild(td);
                for (var i = 0; i < 5; i++) {
                    var convertido;
                    var itemPaint2 = itemNumbersIM[j];
                    itemPaint = itemPaint2[parseInt(lastyear) - i];
                    if ((typeof itemPaint) == 'number') {
                        convertido = itemPaint.toLocaleString(undefined, { style: 'percent', minimumFractionDigits: 1 });
                        if (itemNameIM[j] == "Days Receivables") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                        if (itemNameIM[j] == "Days Inventory") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                        if (itemNameIM[j] == "Days Payables") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                        if (itemNameIM[j] == "Sales Over Working Capital") {
                            convertido = numeral(itemPaint).format('0,0.00');
                        }
                        if (itemNameIM[j] == "Sales Over Net LTAssets") {
                            convertido = numeral(itemPaint).format('0,0.00');
                        }
                        if (itemNameIM[j] == "Sales Over Net Assets") {
                            convertido = numeral(itemPaint).format('0,0.00');
                        }
                    }
                    else {
                        convertido = numeral(Number(itemPaint)).format('0,0');
                    }
                    if (itemPaint > 1000000) {
                        convertido = "Inf";
                    }
                    var thi = document.createElement("th").appendChild(document.createTextNode(convertido));
                    var tdi = document.createElement("td");
                    tdi.id = itemNameIM[j] + i;
                    tdi.appendChild(thi);
                    tr.appendChild(tdi);
                }
                var dummy4 = document.getElementById("tbodyIM");
                dummy4.appendChild(tr);
            }
            /**
    
                MOBILE Investment Management
    
            */
            for (var j = 0; j < itemNumbersIM.length; j++) {
                // Creating table and the header
                var id = document.createElement("id");
                id.setAttribute("class", "table-responsive table-striped w-75 mx-auto");
                var table = document.createElement("table");
                table.setAttribute("class", "table table-hover table-striped w-75 mx-auto");
                var thead = document.createElement("thead");
                thead.setAttribute("class", "thead-dark");
                var tr = document.createElement("tr");
                tr.setAttribute("scope", "col");
                var itenName = document.createElement("th").appendChild(document.createTextNode(itemNameIM[j]));
                var tHeader = document.createElement("th");
                tHeader.setAttribute("scope", "col");
                tHeader.appendChild(itenName);
                var noChar = document.createElement("th").appendChild(document.createTextNode("          "));
                var tHeader2 = document.createElement("th");
                tHeader2.setAttribute("scope", "col");
                tHeader2.appendChild(noChar);
                tr.appendChild(tHeader);
                tr.appendChild(tHeader2);
                thead.appendChild(tr);
                table.appendChild(thead);
                id.appendChild(table);
                // Creating rows of the table per year
                var tbody = document.createElement("tbody");
                for (var i = 0; i < 5; i++) {
                    var convertido;
                    var itemPaint2 = itemNumbersIM[j];
                    var itemPaint = itemPaint2[parseInt(lastyear) - i];
                    if ((typeof itemPaint) == 'number') {
                        convertido = itemPaint.toLocaleString(undefined, { style: 'percent', minimumFractionDigits: 1 });
                        if (itemNameIM[j] == "Days Receivables") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                        if (itemNameIM[j] == "Days Inventory") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                        if (itemNameIM[j] == "Days Payables") {
                            convertido = numeral(itemPaint).format('0,0');
                        }
                        if (itemNameIM[j] == "Sales Over Working Capital") {
                            convertido = numeral(itemPaint).format('0,0.00');
                        }
                        if (itemNameIM[j] == "Sales Over Net LTAssets") {
                            convertido = numeral(itemPaint).format('0,0.00');
                        }
                        if (itemNameIM[j] == "Sales Over Net Assets") {
                            convertido = numeral(itemPaint).format('0,0.00');
                        }
                    }
                    else {
                        convertido = numeral(Number(itemPaint)).format('0,0');
                    }
                    if (itemPaint > 1000000) {
                        convertido = "Inf";
                    }
                    var yeari = parseInt(lastyear) - i;
                    var yeariS = yeari.toString();
                    var year = document.createElement("td").appendChild(document.createTextNode(yeariS));
                    var tdYear = document.createElement("td");
                    tdYear.appendChild(year);
                    var value = document.createElement("td").appendChild(document.createTextNode(convertido));
                    var tdValue = document.createElement("td");
                    tdValue.appendChild(value);
                    var tr = document.createElement("tr");
                    tr.appendChild(tdYear);
                    tr.appendChild(tdValue);
                    tbody.appendChild(tr);
                }
                table.appendChild(tbody);
                var dummy10 = document.getElementById("IMM");
                dummy10.appendChild(table);
            }
            /***********************
        
                Financial Management
    
            */
            var itemNameFM = ["Debt To Capital Ratio",
                "Spread",
                "Net Financial Leverage (NFL)",
                "Financial Leverage (Spread X NFL)",
                "Current Ratio (CA / CL)",
                "Quick Ratio (AR + C / CL)",
                "Cash Ratio (C / CL)",
                "Operating Income vs Interest Expense",
                "Operating Cash Flow vs Interest expense",
            ];
            var itemNumbersFM = [fr.FM.debtToCapitalRatio,
                fr.FM.spread,
                fr.FM.netFinancialLeverage,
                fr.FM.financialLeverage,
                fr.FM.currentRatio,
                fr.FM.quickRatio,
                fr.FM.cashRatio,
                fr.FM.interestCoverage_operatingIncomeVSinterestexpense,
                fr.FM.interestCoverage_operatingCashFlowVSinterestexpense,
            ];
            var bodyFM = document.createElement("tbody");
            bodyFM.setAttribute("id", "tbodyFM");
            var dummy5 = document.getElementById("tableFM");
            dummy5.appendChild(bodyFM);
            var lastyear = lastYear(fr.FM.equity);
            var col_1FM = document.getElementById("col_1FM");
            col_1FM.innerHTML = String(lastyear);
            var col_2FM = document.getElementById("col_2FM");
            col_2FM.innerHTML = String(parseInt(lastyear) - 1);
            var col_3FM = document.getElementById("col_3FM");
            col_3FM.innerHTML = String(parseInt(lastyear) - 2);
            var col_4FM = document.getElementById("col_4FM");
            col_4FM.innerHTML = String(parseInt(lastyear) - 3);
            var col_5FM = document.getElementById("col_5FM");
            col_5FM.innerHTML = String(parseInt(lastyear) - 4);
            for (var j = 0; j < itemNumbersFM.length; j++) {
                var th = document.createElement("th").appendChild(document.createTextNode(itemNameFM[j]));
                var td = document.createElement("td");
                var tr = document.createElement("tr");
                td.appendChild(th);
                tr.appendChild(td);
                for (var i = 0; i < 5; i++) {
                    var convertido;
                    var itemPaint2 = itemNumbersFM[j];
                    itemPaint = itemPaint2[parseInt(lastyear) - i];
                    if ((typeof itemPaint) == 'number') {
                        convertido = itemPaint.toLocaleString(undefined, { style: 'percent', minimumFractionDigits: 1 });
                        if (itemNameFM[j] == "Current Ratio (CA / CL)") {
                            convertido = numeral(itemPaint).format('0,0.00');
                        }
                        if (itemNameFM[j] == "Quick Ratio (AR + C / CL)") {
                            convertido = numeral(itemPaint).format('0,0.00');
                        }
                        if (itemNameFM[j] == "Cash Ratio (C / CL)") {
                            convertido = numeral(itemPaint).format('0,0.00');
                        }
                        if (itemNameFM[j] == "Operating Income vs Interest Expense") {
                            convertido = numeral(itemPaint).format('0,0.00');
                        }
                        if (itemNameFM[j] == "Operating Cash Flow vs Interest expense") {
                            convertido = numeral(itemPaint).format('0,0.00');
                        }
                    }
                    else {
                        convertido = numeral(Number(itemPaint)).format('0,0');
                    }
                    if (itemPaint > 1000000) {
                        convertido = "Inf";
                    }
                    var thi = document.createElement("th").appendChild(document.createTextNode(convertido));
                    var tdi = document.createElement("td");
                    tdi.id = itemNameFM[j] + i;
                    tdi.appendChild(thi);
                    tr.appendChild(tdi);
                }
                var dummy6 = document.getElementById("tbodyFM");
                dummy6.appendChild(tr);
            }
            /**
   
               MOBILE Financial Management
   
           */
            for (var j = 0; j < itemNumbersFM.length; j++) {
                // Creating table and the header
                var id = document.createElement("id");
                id.setAttribute("class", "table-responsive table-striped w-75 mx-auto");
                var table = document.createElement("table");
                table.setAttribute("class", "table table-hover table-striped w-75 mx-auto");
                var thead = document.createElement("thead");
                thead.setAttribute("class", "thead-dark");
                var tr = document.createElement("tr");
                tr.setAttribute("scope", "col");
                var itenName = document.createElement("th").appendChild(document.createTextNode(itemNameFM[j]));
                var tHeader = document.createElement("th");
                tHeader.setAttribute("scope", "col");
                tHeader.appendChild(itenName);
                var noChar = document.createElement("th").appendChild(document.createTextNode("          "));
                var tHeader2 = document.createElement("th");
                tHeader2.setAttribute("scope", "col");
                tHeader2.appendChild(noChar);
                tr.appendChild(tHeader);
                tr.appendChild(tHeader2);
                thead.appendChild(tr);
                table.appendChild(thead);
                id.appendChild(table);
                // Creating rows of the table per year
                var tbody = document.createElement("tbody");
                for (var i = 0; i < 5; i++) {
                    var convertido;
                    var itemPaint2 = itemNumbersFM[j];
                    var itemPaint = itemPaint2[parseInt(lastyear) - i];
                    if ((typeof itemPaint) == 'number') {
                        convertido = itemPaint.toLocaleString(undefined, { style: 'percent', minimumFractionDigits: 1 });
                        if (itemNameFM[j] == "Current Ratio") {
                            convertido = numeral(itemPaint).format('0,0.00');
                        }
                        if (itemNameFM[j] == "Quick Ratio") {
                            convertido = numeral(itemPaint).format('0,0.00');
                        }
                        if (itemNameFM[j] == "Cash Ratio") {
                            convertido = numeral(itemPaint).format('0,0.00');
                        }
                        if (itemNameFM[j] == "Operating Income vs Interest Expense") {
                            convertido = numeral(itemPaint).format('0,0.00');
                        }
                        if (itemNameFM[j] == "Operating Cash Flow vs Interest expense") {
                            convertido = numeral(itemPaint).format('0,0.00');
                        }
                    }
                    else {
                        convertido = numeral(Number(itemPaint)).format('0,0');
                    }
                    if (itemPaint > 1000000) {
                        convertido = "Inf";
                    }
                    var yeari = parseInt(lastyear) - i;
                    var yeariS = yeari.toString();
                    var year = document.createElement("td").appendChild(document.createTextNode(yeariS));
                    var tdYear = document.createElement("td");
                    tdYear.appendChild(year);
                    var value = document.createElement("td").appendChild(document.createTextNode(convertido));
                    var tdValue = document.createElement("td");
                    tdValue.appendChild(value);
                    var tr = document.createElement("tr");
                    tr.appendChild(tdYear);
                    tr.appendChild(tdValue);
                    tbody.appendChild(tr);
                }
                table.appendChild(tbody);
                var dummy10 = document.getElementById("FMM");
                dummy10.appendChild(table);
            }
        });
    });
});
function paint() {
    console.log("XXXXXXX");
}

},{"../../node_modules/numeral/numeral":1}]},{},[2])

//# sourceMappingURL=app.js.map
