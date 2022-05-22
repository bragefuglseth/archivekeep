package spa

import (
	"bytes"
	"errors"
	"fmt"
	"io"
	"io/fs"
	"log"
	"net/http"
	"time"
)

func createWebUIHandler(
	getAsset func(path string) (io.ReadSeeker, time.Time, error),
	getIndex func() (io.ReadSeeker, time.Time, error),
) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		f, modTime, err := getAsset(r.URL.Path)
		if fCloser, ok := f.(io.Closer); ok {
			defer fCloser.Close()
		}

		if err != nil && !errors.Is(err, fs.ErrNotExist) {
			handleError(w, fmt.Errorf("get asset %s: %w", r.URL.Path, err))
			return
		} else if err == nil {
			http.ServeContent(w, r, r.URL.Path, modTime, f)
			return
		}

		f, modTime, err = getIndex()
		if err != nil {
			handleError(w, err)
			return
		}
		if fCloser, ok := f.(io.Closer); ok {
			defer fCloser.Close()
		}

		http.ServeContent(w, r, r.URL.Path, modTime, f)
	}
}

func handleError(w http.ResponseWriter, err error) {
	if errors.Is(err, fs.ErrNotExist) {
		log.Printf("not found error: %v", err)
		w.WriteHeader(http.StatusNotFound)
	} else {
		log.Printf("internal error: %v", err)
		w.WriteHeader(http.StatusInternalServerError)
	}
}

func patchConstants(input io.Reader, config Config) ([]byte, error) {
	contents, err := io.ReadAll(input)
	if err != nil {
		return nil, fmt.Errorf("read contents: %w", err)
	}

	patched := bytes.ReplaceAll(contents, []byte(publicPathPlaceholder), []byte(config.PublicPath))

	return patched, nil
}
